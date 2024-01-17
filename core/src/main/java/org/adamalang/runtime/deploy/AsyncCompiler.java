/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.deploy;

import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.env.RuntimeEnvironment;
import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** the async compiler all unified into one big thing */
public class AsyncCompiler {
  public static String normalizeSpaceNameForClass(String space) {
    return space.replaceAll(Pattern.quote("-"), Matcher.quoteReplacement("_"));
  }

  public static void forge(RuntimeEnvironment runtime, String name, DeploymentFactory prior, DeploymentPlan plan, Deliverer deliverer, TreeMap<Integer, PrivateKeyBundle> keys, AsyncByteCodeCache cache, Callback<DeploymentFactory> callback) {
    ConcurrentHashMap<String, LivingDocumentFactory> factories = new ConcurrentHashMap<>();
    AtomicLong _memoryUsed = new AtomicLong();
    AtomicBoolean failedWithBetterError = new AtomicBoolean(false);
    Runnable successLatch = NoErrorMultiCallbackLatchImpl.WRAP(() -> {
      callback.success(new DeploymentFactory(name, plan, _memoryUsed.get(), new HashMap<>(factories)));
    }, plan.versions.size());
    for (Map.Entry<String, DeployedVersion> entry : plan.versions.entrySet()) {
      DeployedVersion version = entry.getValue();
      LivingDocumentFactory factory = null;
      if (prior != null) {
        if (prior.plan.versions.containsKey(entry.getKey())) {
          if (prior.plan.versions.get(entry.getKey()).equals(version)) {
            factory = prior.factories.get(entry.getKey());
          }
        }
      }
      if (factory == null) {
        // First, we compile the Adama code to java
        try {
          CompilerOptions.Builder builder = CompilerOptions.start();
          if (plan.instrument) {
            builder = builder.instrument();
          }
          final var options = builder.make();
          final var globals = GlobalObjectPool.createPoolWithStdLib(runtime);
          final var state = new EnvironmentState(globals, options);
          final var document = new Document();
          MessageDigest digest = Hashing.sha384();
          digest.update(version.main.getBytes(StandardCharsets.UTF_8));
          for (Map.Entry<String, String> includeEntry : version.includes.entrySet()) {
            digest.update(includeEntry.getKey().getBytes(StandardCharsets.UTF_8));
            digest.update(includeEntry.getValue().getBytes(StandardCharsets.UTF_8));
          }
          String className = normalizeSpaceNameForClass(name) + "_" + Hashing.finishAndEncodeHex(digest);
          document.setClassName(className);
          document.setIncludes(version.includes);
          final var tokenEngine = new TokenEngine("main", version.main.codePoints().iterator());
          final var parser = new Parser(tokenEngine, document.getSymbolIndex(), Scope.makeRootDocument());
          parser.document().accept(document);
          if (!document.check(state.scope())) {
            if (failedWithBetterError.compareAndSet(false, true)) {
              callback.failure(new ErrorCodeException(ErrorCodes.DEPLOYMENT_CANT_TYPE_LANGUAGE, document.errorsJson()));
            }
          } else {
            final var java = document.compileJava(state);
            JsonStreamWriter reflection = new JsonStreamWriter();
            document.writeTypeReflectionJson(reflection);
            cache.fetchOrCompile(name, className, java, reflection.toString(), new ExceptionCallback<>() {
              @Override
              public void invoke(CachedByteCode code) throws ErrorCodeException {
                LivingDocumentFactory newFactory = new LivingDocumentFactory(code, deliverer, keys);
                _memoryUsed.addAndGet(newFactory.memoryUsage);
                factories.put(entry.getKey(), newFactory);
                successLatch.run();
              }

              @Override
              public void failure(ErrorCodeException ex) {
                if (failedWithBetterError.compareAndSet(false, true)) {
                  callback.failure(ex);
                }
              }
            });
          }
        } catch (AdamaLangException ex) {
          if (failedWithBetterError.compareAndSet(false, true)) {
            callback.failure(new ErrorCodeException(ErrorCodes.FACTORY_CANT_BIND_JAVA_CODE));
          }
          return;
        }
      } else {
        factories.put(entry.getKey(), factory);
        _memoryUsed.addAndGet(factory.memoryUsage);
        successLatch.run();
      }
    }
  }
}
