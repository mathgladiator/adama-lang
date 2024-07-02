/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeploymentFactoryTests {

  @Test
  public void cantParse() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":\"@con\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT, RuntimeEnvironment.Tooling);
    CountDownLatch latch = new CountDownLatch(1);
    base.deploy("space", plan, new TreeMap<>(), new Callback<Void>() {
      @Override
      public void success(Void value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.assertEquals(198174, ex.code);
        latch.countDown();
      }
    });
    Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void cantType() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":\"public int x = true;\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT, RuntimeEnvironment.Tooling);

    CountDownLatch latch = new CountDownLatch(1);
    base.deploy("space", plan, new TreeMap<>(), new Callback<Void>() {
      @Override
      public void success(Void value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.assertEquals(132157, ex.code);
        latch.countDown();
      }
    });
    Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void happy() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});
    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT, RuntimeEnvironment.Tooling);
    CountDownLatch latch = new CountDownLatch(1);
    base.deploy("space", plan, new TreeMap<>(), new Callback<Void>() {
      @Override
      public void success(Void value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    });
    Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
    Assert.assertEquals("0w9NHaDbD2fTGSLlHuGyCQ==", base.hashOf("space"));
  }

  @Test
  public void rxhtml() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":{\"main\":\"public int x = 123;\",\"rxhtml\":\"<forest><page uri=\\\"/\\\">Hello World</page></forest>\"}},\"default\":\"x\",\"plan\":[]}",
            (t, errorCode) -> {});
    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT, RuntimeEnvironment.Tooling);
    CountDownLatch latch = new CountDownLatch(1);
    base.deploy("space", plan, new TreeMap<>(), new Callback<Void>() {
      @Override
      public void success(Void value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    });
    Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
    Assert.assertEquals("W9ngBjBRMNDRTZY3N3OjKA==", base.hashOf("space"));
  }

  @Test
  public void happyDirect() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});
    DeploymentFactory newFactory = forge("space", "Space_", new AtomicInteger(1000), null, plan, Deliverer.FAILURE, new TreeMap<>());
    Assert.assertEquals(1, newFactory.spacesAvailable().size());
  }

  @Test
  public void happyInstrument() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"instrument\":true,\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});
    DeploymentFactory newFactory = forge("space", "Space_", new AtomicInteger(1000), null, plan, Deliverer.FAILURE, new TreeMap<>());
    Assert.assertEquals(1, newFactory.spacesAvailable().size());
  }

  public static DeploymentFactory forge(String name, String spacePrefix, AtomicInteger newClassId, DeploymentFactory prior, DeploymentPlan plan, Deliverer deliverer, TreeMap<Integer, PrivateKeyBundle> keys) throws ErrorCodeException {
    HashMap<String, LivingDocumentFactory> factories = new HashMap<>();
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
        factory = compile(name, spacePrefix.replaceAll(Pattern.quote("-"), Matcher.quoteReplacement("_")) + newClassId.getAndIncrement(), entry.getValue().main, entry.getValue().includes, deliverer, keys, plan.instrument);
      }
      _memoryUsed += factory.memoryUsage;
      factories.put(entry.getKey(), factory);
    }
    return new DeploymentFactory(name, plan, _memoryUsed, factories);
  }

  private static LivingDocumentFactory compile(String spaceName, String className, final String code, Map<String, String> includes, Deliverer deliverer, TreeMap<Integer, PrivateKeyBundle> keys, boolean instrument) throws ErrorCodeException {
    try {
      CompilerOptions.Builder builder = CompilerOptions.start();
      if (instrument) {
        builder = builder.instrument();
      }
      final var options = builder.make();
      final var globals = GlobalObjectPool.createPoolWithStdLib(RuntimeEnvironment.Tooling);
      final var state = new EnvironmentState(globals, options);
      final var document = new Document();
      document.setClassName(className);
      document.setIncludes(includes);
      final var tokenEngine = new TokenEngine("main", code.codePoints().iterator());
      final var parser = new Parser(tokenEngine, document.getSymbolIndex(), Scope.makeRootDocument());
      parser.document().accept(document);
      if (!document.check(state.scope())) {
        throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_CANT_TYPE_LANGUAGE, document.errorsJson());
      }
      final var java = document.compileJava(state);
      JsonStreamWriter reflection = new JsonStreamWriter();
      document.writeTypeReflectionJson(reflection);
      return new LivingDocumentFactory(SyncCompiler.compile(spaceName, className, java, reflection.toString()), deliverer, keys);
    } catch (AdamaLangException ex) {
      throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_CANT_PARSE_LANGUAGE, ex);
    }
  }
}
