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
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * converts a DeploymentPlan into a LivingDocumentFactoryFactory; if this can be created, then it is
 * in good order
 */
public class DeploymentFactory implements LivingDocumentFactoryFactory {
  public final String name;
  public final DeploymentPlan plan;
  public final long memoryUsed;
  private final HashMap<String, LivingDocumentFactory> factories;

  /**
   * @param spacePrefix - used for debugging by generating a relevant class name
   * @param newClassId - generates a unique number for the class name
   * @param prior - a prior deployment plan; this caches compilation between updates
   * @param plan - the plan to compile
   * @throws ErrorCodeException
   */
  public DeploymentFactory(String name, String spacePrefix, AtomicInteger newClassId, DeploymentFactory prior, DeploymentPlan plan, Deliverer deliverer, TreeMap<Integer, PrivateKeyBundle> keys) throws ErrorCodeException {
    this.name = name;
    this.factories = new HashMap<>();
    long _memoryUsed = 0L;
    for (Map.Entry<String, DeployedVersion> entry : plan.versions.entrySet()) {
      LivingDocumentFactory factory = null;
      if (prior != null) {
        if (prior.plan.versions.containsKey(entry.getKey())) {
          if (prior.plan.versions.get(entry.getKey()).equals(entry.getValue())) {
            factory = prior.factories.get(entry.getKey());
          }
        }
      }
      if (factory == null) {
        factory = compile(name, spacePrefix.replaceAll(Pattern.quote("-"), Matcher.quoteReplacement("_")) + newClassId.getAndIncrement(), entry.getValue().main, entry.getValue().includes, deliverer, keys);
      }
      _memoryUsed += factory.memoryUsage;
      factories.put(entry.getKey(), factory);
    }
    this.plan = plan;
    this.memoryUsed = _memoryUsed;
  }

  public static LivingDocumentFactory compile(String spaceName, String className, final String code, HashMap<String, String> includes, Deliverer deliverer, TreeMap<Integer, PrivateKeyBundle> keys) throws ErrorCodeException {
    try {
      final var options = CompilerOptions.start().make();
      final var globals = GlobalObjectPool.createPoolWithStdLib();
      final var state = new EnvironmentState(globals, options);
      final var document = new Document();
      document.setClassName(className);
      document.setIncludes(includes);
      final var tokenEngine = new TokenEngine("main", code.codePoints().iterator());
      final var parser = new Parser(tokenEngine, Scope.makeRootDocument());
      parser.document().accept(document);
      if (!document.check(state.scope())) {
        throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_CANT_TYPE_LANGUAGE, document.errorsJson());
      }
      final var java = document.compileJava(state);
      JsonStreamWriter reflection = new JsonStreamWriter();
      document.writeTypeReflectionJson(reflection);
      return new LivingDocumentFactory(spaceName, className, java, reflection.toString(), deliverer, keys);
    } catch (AdamaLangException ex) {
      throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_CANT_PARSE_LANGUAGE, ex);
    }
  }

  @Override
  public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
    String versionToUse = plan.pickVersion(key.key);
    callback.success(factories.get(versionToUse));
  }

  @Override
  public Collection<String> spacesAvailable() {
    return Collections.singleton(name);
  }
}
