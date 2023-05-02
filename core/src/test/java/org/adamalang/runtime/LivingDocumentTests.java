/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.delta.secure.TestKey;
import org.adamalang.runtime.exceptions.GoodwillExhaustedException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.ops.StdOutDocumentMonitor;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.MockDataObserver;
import org.adamalang.support.testgen.DumbDataService;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LivingDocumentTests {
  private static final NtAsset EXAMPLE =
      new NtAsset("42", "file.png", "image/png", 1024, "some hash", "a better hash");
  private static NtPrincipal A = new NtPrincipal("A", "TEST");
  private static NtPrincipal B = new NtPrincipal("B", "TEST");
  private static HashMap<String, LivingDocumentFactory> compilerCache = new HashMap<>();

  public static LivingDocumentFactory compile(final String code, Deliverer deliverer) throws Exception {
    CompilerOptions.Builder opts = CompilerOptions.start().enableCodeCoverage();
    opts.packageName = "P";
    final var options = opts.noCost().make();
    final var globals = GlobalObjectPool.createPoolWithStdLib();
    final var state = new EnvironmentState(globals, options);
    final var document = new Document();
    document.setClassName("MeCode");
    final var tokenEngine = new TokenEngine("<direct code>", code.codePoints().iterator());
    final var parser = new Parser(tokenEngine);
    parser.document().accept(document);
    if (!document.check(state)) {
      throw new Exception("Failed to check:" + document.errorsJson());
    }
    JsonStreamWriter reflection = new JsonStreamWriter();
    document.writeTypeReflectionJson(reflection);
    final var java = document.compileJava(state);
    var cached = compilerCache.get(java);
    if (cached == null) {
      cached = new LivingDocumentFactory("me", "MeCode", java, reflection.toString(), deliverer);
      compilerCache.put(java, cached);
    }
    return cached;
  }

  @Test
  public void bad_json() throws Exception {
    try {
      new RealDocumentSetup(
          "@connected { return @who == @no_one; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set[]> chan; #wait { foreach(x in chan.fetch(@no_one).await()) { t += x.v; }  }",
          "");
      Assert.fail();
    } catch (RuntimeException re) {
    }
  }

  @Test
  public void privacy_flux() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@connected { return true; } public bool x; policy p { return x; } record R { public int z; } use_policy<p> R r; @construct { r.z = 1000; x = true; } message N {} channel foo(N n) { x = !x; }",
          null);
      RealDocumentSetup.GotView gv = new RealDocumentSetup.GotView();
      ArrayList<String> list = new ArrayList<>();
      Perspective linked =
          new Perspective() {
            @Override
            public void data(String data) {
              list.add(data);
            }

            @Override
            public void disconnect() {}
          };

      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      setup.document.createPrivateView(A, linked, new JsonStreamReader("{}"), TestKey.ENCODER, gv);
      for (int k = 0; k < 5; k++) {
        setup.document.send(ContextSupport.WRAP(A), null, "foo", "{}", new RealDocumentSetup.AssertInt(5 + k));
      }
      Assert.assertEquals(6, list.size());
      Assert.assertEquals("{\"data\":{\"x\":true,\"r\":{\"z\":1000}},\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"x\":false,\"r\":null},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"x\":true,\"r\":{\"z\":1000}},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"x\":false,\"r\":null},\"seq\":7}", list.get(3));
      Assert.assertEquals("{\"data\":{\"x\":true,\"r\":{\"z\":1000}},\"seq\":8}", list.get(4));
      Assert.assertEquals("{\"data\":{\"x\":false,\"r\":null},\"seq\":9}", list.get(5));
    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void observe() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@connected { return true; } public bool x; record R { public int z; } public R r; @construct { r.z = 1000; x = true; } message N {} channel foo(N n) { r.z++; x = !x; }",
          null);
      RealDocumentSetup.GotView gv = new RealDocumentSetup.GotView();
      ArrayList<String> list = new ArrayList<>();
      Perspective linked =
          new Perspective() {
            @Override
            public void data(String data) {
              list.add(data);
            }

            @Override
            public void disconnect() {}
          };
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      setup.document.createPrivateView(A, linked, new JsonStreamReader("{}"), TestKey.ENCODER, gv);

      MockDataObserver obs1 = new MockDataObserver();
      setup.document.watch(obs1);
      for (int k = 0; k < 5; k++) {
        setup.document.send(ContextSupport.WRAP(A), null, "foo", "{}", new RealDocumentSetup.AssertInt(5 + k));
      }
      MockDataObserver obs2 = new MockDataObserver();
      setup.document.watch(obs2);
      setup.document.unwatch(obs1);
      for (int k = 0; k < 5; k++) {
        setup.document.send(ContextSupport.WRAP(A), null, "foo", "{}", new RealDocumentSetup.AssertInt(10 + k));
      }
      MockDataObserver obs3 = new MockDataObserver();
      setup.document.watch(obs3);
      Assert.assertEquals(6, obs1.writes.size());
      Assert.assertEquals(6, obs2.writes.size());
      Assert.assertEquals(1, obs3.writes.size());
      obs1.dump("Observer 1");
      obs2.dump("Observer 2");
      obs3.dump("Observer 3");
    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void text() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "public text document; @construct {}",
          "{}");
    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void message_blocked_by_await() throws Exception {
    try {
    RealDocumentSetup setup = new RealDocumentSetup(
        "@connected { return true; }" + //
            "message M { int x; }" + //
            "public int x = 123;" + //
            "channel<M> aq;" + //
            "channel foo(M m) {" + //
            " x += m.x;" + //
            " x += aq.fetch(@no_one).await().x;" + //
            " x += m.x;" + //
            "}" + //
            "", //
        null);
      RealDocumentSetup.GotView gv = new RealDocumentSetup.GotView();
      ArrayList<String> list = new ArrayList<>();
      Perspective linked =
          new Perspective() {
            @Override
            public void data(String data) {
              list.add(data);
            }

            @Override
            public void disconnect() {}
          };
      setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
      setup.document.createPrivateView(NtPrincipal.NO_ONE, linked, new JsonStreamReader("{}"), TestKey.ENCODER, gv);

      setup.document.send(
          ContextSupport.WRAP(NtPrincipal.NO_ONE),
          null,
          "foo",
          "{\"x\":100}",
          new RealDocumentSetup.AssertInt(5));

      setup.document.send(
          ContextSupport.WRAP(NtPrincipal.NO_ONE),
          null,
          "aq",
          "{\"x\":10000}",
          new RealDocumentSetup.AssertInt(7));

      Assert.assertEquals(3, list.size());
      Assert.assertEquals("{\"data\":{\"x\":123},\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"x\":223},\"outstanding\":[{\"id\":1,\"channel\":\"aq\",\"array\":false},{\"id\":2,\"channel\":\"aq\",\"array\":false}],\"blockers\":[{\"agent\":\"?\",\"authority\":\"?\"}],\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"x\":10323},\"outstanding\":[],\"blockers\":[],\"seq\":8}", list.get(2));
    } catch (Exception ex) {
      ex.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void deletion() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@connected { return true; }" +
              "@delete { return true; }",
          null);
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      RealDocumentSetup.AssertSuccess success = new RealDocumentSetup.AssertSuccess();
      setup.document.delete(ContextSupport.WRAP(A), success);
      success.test();
    } catch (RuntimeException re) {
      re.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void deletion_allowed_by_overlord() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@connected { return true; }" +
              "@delete { return false; }",
          null);
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      RealDocumentSetup.AssertSuccess success = new RealDocumentSetup.AssertSuccess();
      setup.document.delete(ContextSupport.WRAP(new NtPrincipal("?", "overlord")), success);
      success.test();
    } catch (RuntimeException re) {
      re.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void deletion_not_allowed_explicit() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@connected { return true; }" +
              "@delete { return false; }",
          null);
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      RealDocumentSetup.AssertJustFailure failure = new RealDocumentSetup.AssertJustFailure(147186);
      setup.document.delete(ContextSupport.WRAP(A), failure);
      failure.test();
    } catch (RuntimeException re) {
      re.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void deletion_not_allowed_implicit() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup("@connected { return true; }", null);
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      RealDocumentSetup.AssertJustFailure failure = new RealDocumentSetup.AssertJustFailure(147186);
      setup.document.delete(ContextSupport.WRAP(A), failure);
      failure.test();
    } catch (RuntimeException re) {
      re.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void service() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@connected { return true; }" +
              "message From { string phone; string msg; }" +
              "message Res { }" +
              "service sms { class=\"demo\"; method<From, Res> send; }" +
              "public string msg = \"123\";" +
              "public formula transmit = sms.send(@no_one, {phone:\"123\",  msg:msg}  );" +
              "",
          null);
      RealDocumentSetup.GotView gv = new RealDocumentSetup.GotView();
      ArrayList<String> list = new ArrayList<>();
      Perspective linked =
          new Perspective() {
            @Override
            public void data(String data) {
              list.add(data);
            }

            @Override
            public void disconnect() {}
          };

      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      setup.document.createPrivateView(A, linked, new JsonStreamReader("{}"), TestKey.ENCODER, gv);
      for (String item : list) {
        System.err.println(item);
      }

    } catch (RuntimeException re) {
      re.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void secure_service() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@connected { return true; }" +
              "message From { string phone; string msg; }" +
              "message Res { }" +
              "service sms { class=\"demo\"; method secured<From, Res> send; }" +
              "public string msg = \"123\";" +
              "message M {}" +
              "channel foo(M m) {" +
              "secure<principal> w = @who;" +
              "sms.send(w, {phone:\"\",msg:\"\"});" +
              "}",
          null);
      RealDocumentSetup.GotView gv = new RealDocumentSetup.GotView();
      ArrayList<String> list = new ArrayList<>();
      Perspective linked =
          new Perspective() {
            @Override
            public void data(String data) {
              list.add(data);
            }

            @Override
            public void disconnect() {}
          };

      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      setup.document.createPrivateView(A, linked, new JsonStreamReader("{}"), TestKey.ENCODER, gv);
      for (String item : list) {
        System.err.println(item);
      }

    } catch (RuntimeException re) {
      re.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void accept_array_message() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected { return @who == @no_one; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set[]> chan; #wait { foreach(x in chan.fetch(@no_one).await()) { t += x.v; }  }");
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    Assert.assertEquals(
        0,
        ((int)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("t")));
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE),
        null,
        "chan",
        "[{\"v\":1000},{\"v\":100000},{\"v\":1}]",
        new RealDocumentSetup.AssertInt(4));
    Assert.assertEquals(
        101001,
        ((int)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("t")));
  }

  @Test
  public void reject_queued_message() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected { return @who == @no_one; } @construct { } private int t = 0; message Set { int v; } channel<Set> chan; channel foo(Set x) { t = 100; t = chan.fetch(@who).await().v; abort; }");
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE),
        "marker-1",
        "foo",
        "{\"v\":1000}",
        new RealDocumentSetup.AssertInt(4));
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE),
        "marker-2",
        "chan",
        "{\"v\":1000}",
        new RealDocumentSetup.AssertInt(6));
  }

  @Test
  public void blocking_test() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@construct { transition #foo; } #foo { block; } test ZOO { @step; assert @blocked; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals(
        "TEST[ZOO] = 100.0%\n" + "...DUMP:{\"__blocked\":true}\n", report.toString());
  }

  @Test
  public void load_test() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x = 100; @construct { x = 100; } @load { x++; }", "{\"x\":1000}");
    Assert.assertEquals(
        1001,
        ((int)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("x")));
  }


  @Test
  @SuppressWarnings("unchecked")
  public void preempt() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int v; @construct { v = 1; transition #foo; } #foo { v = 2; preempt #zoo; block; } #zoo { v = 3; } ");
    Assert.assertEquals(
        2,
        ((int)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("v")));
  }

  @Test
  public void command_unknown() throws Exception {
    String json;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      json = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", json);
    final var writer = setup.document.forge("nope", A);
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(132116, drre.code);
    }
  }

  @Test
  public void construct_requirements_must_have_arg() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    final var document = setup.factory.create(new StdOutDocumentMonitor());
    final var writer = setup.document.forge("construct", A);
    writer.endObject();
    try {
      document.__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(196624, drre.code);
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void futures_blocked() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected { return @who == @no_one; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> chan; #wait { t = chan.fetch(@no_one).await().v; }");
    Assert.assertTrue(
        (Boolean)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("__blocked"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void futures_blocked_still_blocked_wrong_user() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> chan; #wait { t = chan.fetch(@no_one).await().v; }");
    Assert.assertTrue(
        (Boolean)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("__blocked"));
    setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
    setup.document.send(ContextSupport.WRAP(A), null, "chan", "{\"v\":74}", new RealDocumentSetup.AssertInt(4));
    Assert.assertTrue(
        (Boolean)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("__blocked"));
    setup.assertCompare();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void futures_blocked_then_unblocked() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> chan; #wait { t = chan.fetch(@no_one).await().v; }");
    Assert.assertTrue(
        (Boolean)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("__blocked"));
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "chan", "{\"v\":74}", new RealDocumentSetup.AssertInt(4));
    Assert.assertFalse(
        (Boolean)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("__blocked"));
    setup.assertCompare();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void futures_hydrate_missing_data() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> cha; channel<Set> chb; #wait { t = cha.fetch(@no_one).await().v; t += chb.fetch(@no_one).await().v; }",
            "{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__blocked_on\":\"cha\",\"__blocked\":true,\"__seq\":5,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__messages\":{\"0\":{\"nope\":true,\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"channel\":\"chb\",\"message\":{\"v\":50}}},\"__message_id\":1}");
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "cha", "{\"v\":25}", new RealDocumentSetup.AssertInt(6));
    Assert.assertFalse(
        (Boolean)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("__blocked"));
    setup.assertCompare();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void futures_out_of_order_rehydrate() throws Exception {
    String persist;
    {
      final var setup =
          new RealDocumentSetup(
              "@connected { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> cha; channel<Set> chb; #wait { t = cha.fetch(@no_one).await().v; t += chb.fetch(@no_one).await().v; }");
      setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
      setup.document.send(
          ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "chb", "{\"v\":50}", new RealDocumentSetup.AssertInt(4));
      persist = setup.document.json();
      setup.assertCompare();
      Assert.assertTrue(
          (Boolean)
              ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                  .get("__blocked"));
    }
    final var setup =
        new RealDocumentSetup(
            "@connected { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> cha; channel<Set> chb; #wait { t = cha.fetch(@no_one).await().v; t += chb.fetch(@no_one).await().v; }",
            persist);
    Assert.assertTrue(
        (Boolean)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("__blocked"));
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "cha", "{\"v\":25}", new RealDocumentSetup.AssertInt(6));
    Assert.assertFalse(
        (Boolean)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("__blocked"));

    setup.assertCompare();
  }

  @Test
  public void infinite_loop_1() throws Exception {
    var gotIt = false;
    try {
      final var setup = new RealDocumentSetup("@construct { while(true) {} }");
      Assert.fail();
    } catch (final RuntimeException yay) {
      Throwable search = yay;
      while (!(search instanceof GoodwillExhaustedException)) {
        search = search.getCause();
      }
      Assert.assertTrue(search instanceof GoodwillExhaustedException);
      yay.printStackTrace();
      gotIt = true;
    }
    Assert.assertTrue(gotIt);
  }

  @Test
  public void infinite_loop_2() throws Exception {
    var gotIt = false;
    try {
      new RealDocumentSetup("@construct { transition #loop; } #loop { while(true) {} }");
      Assert.fail();
    } catch (final RuntimeException yay) {
      Throwable search = yay;
      while (!(search instanceof GoodwillExhaustedException)) {
        search = search.getCause();
      }
      Assert.assertTrue(search instanceof GoodwillExhaustedException);
      yay.printStackTrace();
      gotIt = true;
    }
    Assert.assertTrue(gotIt);
  }

  @Test
  public void apply_patch() throws Exception {
    final var setup = new RealDocumentSetup("int x;");
    setup.document.apply(NtPrincipal.NO_ONE, "{\"x\":4242}", new RealDocumentSetup.AssertInt(2));
    Assert.assertTrue(setup.document.json().contains("\"x\":4242"));
  }

  @Test
  public void deploy() throws Exception {
    final var setup = new RealDocumentSetup("public int x;");
    RealDocumentSetup.GotView gv = new RealDocumentSetup.GotView();
    ArrayList<String> list = new ArrayList<>();
    Perspective linked =
        new Perspective() {
          @Override
          public void data(String data) {
            list.add(data);
          }

          @Override
          public void disconnect() {}
        };
    setup.document.createPrivateView(NtPrincipal.NO_ONE, linked, new JsonStreamReader("{}"), TestKey.ENCODER, gv);
    setup.document.apply(NtPrincipal.NO_ONE, "{\"x\":4242}", new RealDocumentSetup.AssertInt(3));
    setup.document.deploy(
        new RealDocumentSetup("public formula x = 50;").factory,
        new RealDocumentSetup.AssertInt(4));
    Assert.assertEquals(3, list.size());
    Assert.assertEquals("{\"data\":{\"x\":0},\"seq\":2}", list.get(0));
    Assert.assertEquals("{\"data\":{\"x\":4242},\"seq\":3}", list.get(1));
    Assert.assertEquals("{\"data\":{\"x\":50},\"seq\":4}", list.get(2));
  }

  @Test
  public void infinite_loop_bubble() throws Exception {
    var gotIt = false;
    try {
      final var setup =
          new RealDocumentSetup(
              "@connected { return true; } function inf() -> int { int z = 0; while (z < 10000000) { z++; } return z; } bubble x = inf();");
      setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
      setup.document.createPrivateView(NtPrincipal.NO_ONE, Perspective.DEAD, new JsonStreamReader("{}"), TestKey.ENCODER, new RealDocumentSetup.GotView());
      setup.document.invalidate(new RealDocumentSetup.AssertInt(5));
      Assert.fail();
    } catch (final RuntimeException yay) {
      Throwable search = yay;
      while (!(search instanceof GoodwillExhaustedException)) {
        search = search.getCause();
      }
      Assert.assertTrue(search instanceof GoodwillExhaustedException);
      yay.printStackTrace();
      gotIt = true;
    }
    Assert.assertTrue(gotIt);
  }

  @Test
  public void infinite_loop_bubble_no_monitor() throws Exception {
    var gotIt = false;
    try {
      final var setup =
          new RealDocumentSetup(
              "@connected { return true; } function inf() -> int { int z = 0; while (z < 10000000) { z++; } return z; } bubble x = inf();",
              null,
              false);
      setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
      setup.document.createPrivateView(NtPrincipal.NO_ONE, Perspective.DEAD, new JsonStreamReader("{}"), TestKey.ENCODER, new RealDocumentSetup.GotView());
      setup.document.invalidate(new RealDocumentSetup.AssertInt(5));
      Assert.fail();
    } catch (final RuntimeException yay) {
      Throwable search = yay;
      while (!(search instanceof GoodwillExhaustedException)) {
        search = search.getCause();
      }
      Assert.assertTrue(search instanceof GoodwillExhaustedException);
      yay.printStackTrace();
      gotIt = true;
    }
    Assert.assertTrue(gotIt);
  }

  @Test
  public void enum_fix_rx() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "enum EEE { A:100, B, C } public EEE x; @connected { return true; } ",
            "{\"x\":-123}",
            false);
    RealDocumentSetup.GotView gv = new RealDocumentSetup.GotView();
    ArrayList<String> list = new ArrayList<>();
    Perspective linked =
        new Perspective() {
          @Override
          public void data(String data) {
            list.add(data);
          }

          @Override
          public void disconnect() {}
        };
    setup.document.createPrivateView(NtPrincipal.NO_ONE, linked, new JsonStreamReader("{}"), TestKey.ENCODER, gv);
    Assert.assertEquals(1, list.size());
    Assert.assertEquals("{\"data\":{\"x\":100},\"seq\":1}", list.get(0));
  }

  @Test
  public void enum_fix_msg() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "enum EEE { A:100, B, C } public EEE x; public int y = 0; @connected { return true; } message M { EEE eee; } channel foo(M m) { x = m.eee; y = m.eee.to_int(); }",
            "{\"x\":101}",
            false);
    RealDocumentSetup.GotView gv = new RealDocumentSetup.GotView();
    ArrayList<String> list = new ArrayList<>();
    Perspective linked =
        new Perspective() {
          @Override
          public void data(String data) {
            list.add(data);
          }

          @Override
          public void disconnect() {}
        };
    setup.document.createPrivateView(NtPrincipal.NO_ONE, linked, new JsonStreamReader("{}"), TestKey.ENCODER, gv);
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{\"eee\":10000}", new RealDocumentSetup.AssertInt(4));
    Assert.assertEquals(3, list.size());
    Assert.assertEquals("{\"data\":{\"x\":101,\"y\":0},\"seq\":1}", list.get(0));
    Assert.assertEquals("{\"seq\":3}", list.get(1));
    Assert.assertEquals("{\"data\":{\"x\":100,\"y\":100},\"seq\":4}", list.get(2));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void invoke_random() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "double d1; double d2; int i1; int i2; long l; int z; @construct { d1 = Random.genDouble(); d2 = Random.getDoubleGaussian() * 6; i1 = Random.genInt(); i2 = Random.genBoundInt(50); l = Random.genLong(); z = Random.genBoundInt(-1); }");
    String d1 =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("d1")
            .toString();
    String d2 =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("d2")
            .toString();
    String i1 =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("i1")
            .toString();
    String i2 =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("i2")
            .toString();
    String l =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("l")
            .toString();
    Assert.assertEquals("0.7231742029971469", d1);
    Assert.assertEquals("2.6429286547789945", d2);
    Assert.assertEquals("-535098017", i1);
    Assert.assertEquals("26", i2);
    Assert.assertEquals("-5237980416576129062", l);
    setup.assertCompare();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void invoke_time() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "long x; @construct { x = Time.now(); }", null, true, new MockTime(450));
    setup.document.invalidate(new RealDocumentSetup.AssertInt(2));
    String x =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("x")
            .toString();
    String __time =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("__time")
            .toString();
    Assert.assertEquals("450", __time);
    Assert.assertEquals("450", x);
    setup.assertCompare();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void message_cause_rewind() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { Document.rewind(1); }",
            null,
            false);
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{}", new RealDocumentSetup.AssertInt(4));
    String x =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("x")
            .toString();
    Assert.assertEquals("1000", x);
    setup.assertCompare();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void simple_direct_message() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x = 123; }",
            null,
            false);
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{}", new RealDocumentSetup.AssertInt(4));
    String x =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("x")
            .toString();
    Assert.assertEquals("123", x);
    setup.assertCompare();
  }

  @Test
  public void message_cause_rewind_failure() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { Document.rewind(1); }",
            null,
            false);
    ((DumbDataService) setup.document.base.service).computesWork = false;
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{}", new RealDocumentSetup.AssertFailure(23456));
  }

  @Test
  public void message_cause_self_destruct() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { Document.destroy(); }",
            null,
            false);
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{}", new RealDocumentSetup.AssertFailure(134195));
    Assert.assertTrue(
        ((DumbDataService) setup.document.base.service).deleted.contains(new Key("space", "0")));
  }

  @Test
  public void message_cause_self_destruct_but_fails() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { Document.destroy(); }",
            null,
            false);
    ((DumbDataService) setup.document.base.service).deletesWork = false;
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{}", new RealDocumentSetup.AssertFailure(1234567));
  }

  @Test
  public void too_many_messages() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { }",
            null,
            false);
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    ((DumbDataService) setup.document.base.service).dropPatches = true;
    for (int k = 0; k <= 128; k++) {
      setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{}", new RealDocumentSetup.AssertNoResponse());
    }
    for (int j = 0; j <= 127; j++) {
      setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{}", new RealDocumentSetup.AssertFailure(123004));
    }
    for (int j = 0; j < 128; j++) {
      setup.document.send(
          ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{}", new RealDocumentSetup.AssertFailure(192639));
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void message_abort() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x = 100; abort; }",
            null,
            false);
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{}", new RealDocumentSetup.AssertFailure(127152));
  }

  @Test
  public void message_dedupe() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }");
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), "send1", "foo", "{}", new RealDocumentSetup.AssertInt(4));
    setup.assertCompare();
    String x =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("x")
            .toString();
    Assert.assertEquals("142", x);
    try {
      setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), "send1", "foo", "{}", new RealDocumentSetup.AssertInt(5));
      Assert.fail();
    } catch (RuntimeException ece) {
      Assert.assertEquals(143407, ((ErrorCodeException) ece.getCause()).code);
    }
    String z =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("x")
            .toString();
    Assert.assertEquals("142", z);
  }

  @Test
  public void message_expire_single() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }");
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), "send1", "foo", "{}", new RealDocumentSetup.AssertInt(4));
    Assert.assertEquals(
        "142",
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("x")
            .toString());
    HashMap<String, Object> mapSend1 =
        ((HashMap<String, Object>)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("__dedupe"));
    Assert.assertEquals(1, mapSend1.size());
    setup.time.time += 1000;
    setup.document.expire(2000);
    HashMap<String, Object> mapSend2 =
        ((HashMap<String, Object>)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("__dedupe"));
    Assert.assertEquals(1, mapSend1.size());
    setup.document.expire(500);
    HashMap<String, Object> mapSend3 =
        ((HashMap<String, Object>)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("__dedupe"));
    Assert.assertNull(mapSend3);
  }

  @Test
  public void message_expire_negative_limit() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }");
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), "send1", "foo", "{}", new RealDocumentSetup.AssertInt(4));
    setup.document.expire(-2000);
  }

  @Test
  public void message_expire_multi() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }");
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), "send1", "foo", "{}", new RealDocumentSetup.AssertInt(4));
    setup.time.time += 250;
    setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), "send2", "foo", "{}", new RealDocumentSetup.AssertInt(5));
    setup.time.time += 250;
    setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), "send3", "foo", "{}", new RealDocumentSetup.AssertInt(6));
    Assert.assertEquals(
        "342",
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("x")
            .toString());

    int[] size = new int[] {3, 3, 3, 2, 2, 2, 1, 1, 0, 0};
    HashSet<Integer> shouldFail = new HashSet<>();
    shouldFail.add(0);
    shouldFail.add(1);
    shouldFail.add(3);
    shouldFail.add(4);
    shouldFail.add(6);
    shouldFail.add(8);
    shouldFail.add(9);
    int expectedAt = 8;
    Assert.assertEquals(534, setup.document.getMemoryBytes());
    for (int k = 0; k < size.length; k++) {
      HashMap<String, Object> send =
          ((HashMap<String, Object>)
              ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                  .get("__dedupe"));
      if (size[k] == 0) {
        Assert.assertNull(send);
      } else {
        Assert.assertEquals(size[k], send.size());
      }
      setup.time.time += 100;
      if (shouldFail.contains(k)) {
        setup.document.expire(750);
      } else {
        setup.document.expire(750);
        expectedAt += 2;
      }
    }
    Assert.assertEquals(432, setup.document.getMemoryBytes());
  }

  @Test
  public void test_invoke() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "int x = 0; #whoop { x = 123; } test ZOO { invoke #whoop; assert x == 123; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals("TEST[ZOO] = 100.0%\n" + "...DUMP:{\"x\":123}\n", report.toString());
    setup.assertCompare();
  }

  @Test
  public void test_invoke_no_monitor() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "int x = 0; #whoop { x = 123; } test ZOO { invoke #whoop; assert x == 123; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, null, "42");
    Assert.assertEquals("TEST[ZOO] = 100.0%\n" + "...DUMP:{\"x\":123}\n", report.toString());
    setup.assertCompare();
  }

  @Test
  public void pump_abort_rollback() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "message M { int z; } int x = 0; channel foo(M m) { x = m.z; abort; } test ZOO { @pump {z:123} into foo; @step; assert x == 0; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals("TEST[ZOO] = 100.0%\n", report.toString());
    setup.assertCompare();
  }

  @Test
  public void pump_test() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "message M { int z; } int x = 0; channel foo(M m) { x = m.z; } test ZOO { @pump {z:123} into foo; @step; assert x == 123; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals("TEST[ZOO] = 100.0%\n", report.toString());
    setup.assertCompare();
  }

  @Test
  public void run_tests() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "test FOO {} test GOO { assert true; } test ZOO { @step; assert false; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals(
        "TEST[FOO] HAS NO ASSERTS\n" + "TEST[GOO] = 100.0%\n" + "TEST[ZOO] = 0.0% (HAS FAILURES)\n",
        report.toString());
    setup.assertCompare();
  }

  @Test
  public void send_must_be_connected() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@construct {} @connected { return true; } message M {} channel<M> foo;");
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{}", new RealDocumentSetup.AssertFailure(143373));
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{}", new RealDocumentSetup.AssertInt(4));
    setup.assertCompare();
  }

  /** a live issue was found when trying to parse a message array which actually had a string array; caused an infinite loop */
  @Test
  public void coe20221215() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "message D {\n" + //
                "  int id;\n" + //
                "  map<string, dynamic> properties;\n" + //
                "}\n" + //
                "message B {\n" + //
                "  int c;\n" + //
                "  D[] d;  \n" + //
                "}\n" +
                "channel on_b(B batch) {}");
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "on_b", "{\"c\":1,\"d\":[\"a\"]}", new RealDocumentSetup.AssertFailure(145627));
  }

  @Test
  public void blind_send_with_policy() throws Exception {
    final var setup = new RealDocumentSetup("@static { send { return true; } } @construct {} @connected { return true; } message M {} channel<M> foo;");
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{}", new RealDocumentSetup.AssertInt(2));
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(4));
    setup.document.send(ContextSupport.WRAP(NtPrincipal.NO_ONE), null, "foo", "{}", new RealDocumentSetup.AssertInt(6));
    setup.assertCompare();
  }

  @Test
  public void multi_views() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @construct { x = 123; } @connected { x++; return true; }");

    RealDocumentSetup.GotView pv1 = new RealDocumentSetup.GotView();
    RealDocumentSetup.GotView pv2 = new RealDocumentSetup.GotView();
    RealDocumentSetup.GotView pv3 = new RealDocumentSetup.GotView();

    Assert.assertFalse(setup.document.isConnected(NtPrincipal.NO_ONE));
    RealDocumentSetup.ArrayPerspective viewOne = new RealDocumentSetup.ArrayPerspective();
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.createPrivateView(NtPrincipal.NO_ONE, viewOne, new JsonStreamReader("{}"), TestKey.ENCODER, pv1);
    Assert.assertEquals(1, viewOne.datum.size());
    Assert.assertTrue(setup.document.isConnected(NtPrincipal.NO_ONE));

    RealDocumentSetup.ArrayPerspective viewTwo = new RealDocumentSetup.ArrayPerspective();
    setup.document.createPrivateView(NtPrincipal.NO_ONE, viewTwo, new JsonStreamReader("{}"), TestKey.ENCODER, pv2);
    Assert.assertEquals(2, viewOne.datum.size());
    Assert.assertEquals(1, viewTwo.datum.size());

    RealDocumentSetup.ArrayPerspective viewThree = new RealDocumentSetup.ArrayPerspective();
    setup.document.createPrivateView(NtPrincipal.NO_ONE, viewThree, new JsonStreamReader("{}"), TestKey.ENCODER, pv3);
    Assert.assertEquals(3, viewOne.datum.size());
    Assert.assertEquals(2, viewTwo.datum.size());
    Assert.assertEquals(1, viewThree.datum.size());

    Assert.assertEquals(3, setup.document.garbageCollectPrivateViewsFor(NtPrincipal.NO_ONE));
    pv1.view.kill();
    pv3.view.kill();
    Assert.assertEquals(1, setup.document.garbageCollectPrivateViewsFor(NtPrincipal.NO_ONE));
    setup.document.invalidate(new RealDocumentSetup.AssertInt(7));
    pv2.view.kill();
    Assert.assertEquals(0, setup.document.garbageCollectPrivateViewsFor(NtPrincipal.NO_ONE));
    setup.document.disconnect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(8));
    setup.assertCompare();
  }

  @Test
  public void cant_double_construct() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      prior = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", prior);
    final var writer = setup.document.forge("construct", A);
    writer.writeObjectFieldIntro("arg");
    writer.injectJson("{}");
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(132111, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void send_requirements_must_have_channel() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      prior = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", prior);
    final var writer = setup.document.forge("send", A);
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(160268, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void send_requirements_must_have_message() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      prior = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", prior);
    final var writer = setup.document.forge("send", A);
    writer.writeObjectFieldIntro("channel");
    writer.writeFastString("foo");
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(184332, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void send_requirements_must_have_context() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      prior = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", prior);
    final var writer = setup.document.forge("send", A);
    writer.writeObjectFieldIntro("channel");
    writer.writeFastString("foo");
    writer.writeObjectFieldIntro("message");
    writer.writeFastString("{}");
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(127155, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void send_requirements_must_have_who() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      prior = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", prior);
    final var writer = setup.document.forge("send", null);
    writer.writeObjectFieldIntro("channel");
    writer.writeFastString("foo");
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(184332, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void connect_requirements_must_have_who() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      prior = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", prior);
    final var writer = setup.document.forge("connect", null);
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(127155, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void disconnect_requirements_must_have_who() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      prior = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", prior);
    final var writer = setup.document.forge("disconnect", null);
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(127155, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void attach_requirements_must_have_who() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      prior = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", prior);
    final var writer = setup.document.forge("attach", null);
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(127155, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void apply_requirements_must_have_who() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      prior = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", prior);
    final var writer = setup.document.forge("apply", null);
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(122896, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void expire_requirements_must_have_limit() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      prior = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", prior);
    final var writer = setup.document.forge("expire", null);
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(146448, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void apply_requirements_must_have_patch() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      prior = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", prior);
    final var writer = setup.document.forge("apply", A);
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(193055, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void state_machine_progress() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected { return @who == @no_one; } @construct { transition #next; } int t = 0; #next { t++; if (t == 10) { transition #end; } else { transition #next; } } #end {}");
    setup.document.invalidate(Callback.DONT_CARE_INTEGER);
    String t =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("t")
            .toString();
    Assert.assertEquals("10", t);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void state_machine_progress_no_monitor() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected { return @who == @no_one; } @construct { transition #next; } int t = 0; #next { t++; if (t == 10) { transition #end; } else { transition #next; } } #end {}",
            null,
            false);
    setup.document.invalidate(Callback.DONT_CARE_INTEGER);
    String t =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("t")
            .toString();
    setup.document.invalidate(new RealDocumentSetup.AssertInt(12));
    Assert.assertEquals("10", t);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void state_machine_progress_over_time() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected { return @who == @no_one; } @construct { transition #next; } int t = 0; #next { t++; if (t == 10) { transition #end; } else { transition #next in 0.25; } } #end {}");
    for (int k = 0; k < 26; k++) {
      setup.time.time += 100;
      setup.document.invalidate(new RealDocumentSetup.AssertInt(2 + k));
      String state =
          ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
              .get("__state")
              .toString();
      System.err.println(state + "@" + k);
      Assert.assertEquals("next", state);
    }
    setup.document.invalidate(new RealDocumentSetup.AssertInt(28));
    setup.time.time += 100;
    setup.document.invalidate(new RealDocumentSetup.AssertInt(29));
    String t =
        ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
            .get("t")
            .toString();
    Assert.assertEquals("10", t);
  }

  @Test
  public void construct_over_time() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected { return @who == @no_one; } @construct { transition #next in 0.25; } int t = 0; #next { t++; if (t == 10) { transition #end; } else { transition #next in 0.25; } } #end {}");
    setup.document.invalidate(Callback.DONT_CARE_INTEGER);
    Integer bumpTimeMs;
    int k = 3;
    while ((bumpTimeMs = setup.document.getAndCleanRequiresInvalidateMilliseconds()) != null) {
      setup.time.time += bumpTimeMs;
      setup.document.invalidate(new RealDocumentSetup.AssertInt(k));
      k++;
    }
    Assert.assertEquals(13, k);
  }

  @Test
  public void transact() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    setup.assertCompare();
  }

  @Test
  public void transact_add_client_allowed() throws Exception {
    final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
    setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
    setup.document.disconnect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(4));
    setup.assertCompare();
  }

  @Test
  public void transact_add_client_cant_connect_again_but_only_after_disconnect() throws Exception {
    final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
    setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
    setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertFailure(115724));
    setup.document.disconnect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(4));
    setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(6));
    setup.assertCompare();
  }

  @Test
  public void transact_add_client_not_allowed() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertFailure(184333));
    setup.assertCompare();
  }

  @Test
  public void attach_requires_connection() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    setup.document.attach(ContextSupport.WRAP(NtPrincipal.NO_ONE), EXAMPLE, new RealDocumentSetup.AssertFailure(125966));
  }

  @Test
  public void can_attach1() throws Exception {
    final var setup = new RealDocumentSetup("int x;");
    Assert.assertFalse(setup.document.canAttach(ContextSupport.WRAP(NtPrincipal.NO_ONE)));
  }

  @Test
  public void can_attach2() throws Exception {
    final var setup = new RealDocumentSetup("@can_attach { return true; }");
    Assert.assertTrue(setup.document.canAttach(ContextSupport.WRAP(NtPrincipal.NO_ONE)));
  }

  @Test
  public void attach_default() throws Exception {
    final var setup =
        new RealDocumentSetup(
            " public int x = 0; public asset f; @connected { x++; return true; } @construct {} @attached (a) { x++; f = a; }");
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    final var deNO_ONE = new RealDocumentSetup.ArrayPerspective();
    setup.document.createPrivateView(NtPrincipal.NO_ONE, deNO_ONE, new JsonStreamReader("{}"), TestKey.ENCODER, new RealDocumentSetup.GotView());
    Assert.assertEquals(1, deNO_ONE.datum.size());
    Assert.assertTrue(deNO_ONE.datum.get(0).startsWith("{\"data\":{\"x\":1,\"f\":{\""));
    Assert.assertTrue(deNO_ONE.datum.get(0).endsWith("\",\"size\":\"0\",\"type\":\"\",\"md5\":\"\",\"sha384\":\"\"}},\"seq\":4}"));
    setup.document.attach(ContextSupport.WRAP(NtPrincipal.NO_ONE), EXAMPLE, new RealDocumentSetup.AssertInt(5));
    Assert.assertEquals(2, deNO_ONE.datum.size());
    Assert.assertTrue(deNO_ONE.datum.get(1).startsWith("{\"data\":{\"x\":2,\"f\":{\""));
    Assert.assertTrue(deNO_ONE.datum.get(1).endsWith("\",\"size\":\"1024\",\"type\":\"image/png\",\"md5\":\"some hash\",\"sha384\":\"a better hash\"}},\"seq\":5}"));
    setup.assertCompare();
    Assert.assertEquals(1024, setup.dumbDataService.assetsSeen);
  }

  @Test
  public void get_key_and_seq() throws Exception {
    final var setup =
        new RealDocumentSetup(
            " public int lolseq = 0; public string lolkey; @connected { lolseq = Document.seq(); return true; } @construct { lolkey = Document.key(); } ");
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    final var deNO_ONE = new RealDocumentSetup.ArrayPerspective();
    setup.document.createPrivateView(NtPrincipal.NO_ONE, deNO_ONE, new JsonStreamReader("{}"), TestKey.ENCODER, new RealDocumentSetup.GotView());
    Assert.assertEquals(1, deNO_ONE.datum.size());
    Assert.assertEquals("{\"data\":{\"lolseq\":1,\"lolkey\":\"0\"},\"seq\":4}", deNO_ONE.datum.get(0));
    setup.assertCompare();
  }

  @Test
  public void attach_no_asset() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    final var writer = setup.document.forgeWithContext("attach", ContextSupport.WRAP(A));
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
      Assert.fail();
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(143380, drre.code);
    }
  }

  @Test
  public void invalid_arg() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    final var writer = setup.document.forge("nope", A);
    writer.writeObjectFieldIntro("nOPP");
    writer.writeInteger(112);
    writer.endObject();
    try {
      setup.document.document().__transact(writer.toString(), setup.factory);
      Assert.fail();
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(184335, drre.code);
    }
  }

  @Test
  public void transact_disconnect_without_connection() throws Exception {
    final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
    setup.document.disconnect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertFailure(145423));
    setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
    setup.document.disconnect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(4));
    setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(6));
    setup.document.disconnect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(8));
    setup.assertCompare();
  }

  @Test
  public void crash_infinite_transition() throws Exception {
      final var setup = new RealDocumentSetup("@construct { } #next { while(true) {} } @connected { transition #next; return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertFailure(950384));
  }

  @Test
  public void transact_double_construct_document() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      prior = setup.document.json();
      setup.assertCompare();
    }
    {
      final var setup =
          new RealDocumentSetup("@construct {} @connected { return true; }", prior);
      final var document = setup.factory.create(new StdOutDocumentMonitor());
      document.__insert(new JsonStreamReader(prior.toString()));
      final var writer = setup.document.forge("construct", A);
      writer.writeObjectFieldIntro("arg");
      writer.beginObject();
      writer.endObject();
      writer.endObject();
      try {
        document.__transact(writer.toString(), setup.factory);
        Assert.fail();
      } catch (final ErrorCodeException drre) {
        Assert.assertEquals(132111, drre.code);
      }
    }
  }

  @Test
  public void transact_hydrate_clients() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected { return true; }");
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      prior = setup.document.json();
      setup.assertCompare();
    }
    {
      final var setup =
          new RealDocumentSetup("@construct {} @connected { return true; }", prior);
      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertFailure(115724));
      setup.document.connect(ContextSupport.WRAP(B), new RealDocumentSetup.AssertInt(4));
    }
  }

  @Test
  public void transact_requirements_must_have_command() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    final var document = setup.factory.create(new StdOutDocumentMonitor());
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    writer.endObject();
    try {
      document.__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(194575, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void transact_requirements_must_have_timestamp() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    final var document = setup.factory.create(new StdOutDocumentMonitor());
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("command");
    writer.writeString("noop");
    writer.endObject();
    try {
      document.__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(143889, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void transact_requirements_must_have_who_for_construct() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    final var document = setup.factory.create(new StdOutDocumentMonitor());
    final var writer = setup.document.forge("construct", null);
    writer.endObject();
    try {
      document.__transact(writer.toString(), setup.factory);
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(196624, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void views() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @construct { x = 123; } @connected { x++; return true; }");
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    final var deNO_ONE = new RealDocumentSetup.ArrayPerspective();
    setup.document.createPrivateView(NtPrincipal.NO_ONE, deNO_ONE, new JsonStreamReader("{}"), TestKey.ENCODER, new RealDocumentSetup.GotView());
    Assert.assertEquals(1, deNO_ONE.datum.size());
    Assert.assertEquals("{\"data\":{\"x\":124},\"seq\":4}", deNO_ONE.datum.get(0).toString());
    final var deA = new RealDocumentSetup.ArrayPerspective();
    final var deB = new RealDocumentSetup.ArrayPerspective();
    setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(5));
    setup.document.createPrivateView(A, deA, new JsonStreamReader("{}"), TestKey.ENCODER, new RealDocumentSetup.GotView());
    setup.document.connect(ContextSupport.WRAP(B), new RealDocumentSetup.AssertInt(8));
    setup.document.createPrivateView(B, deB, new JsonStreamReader("{}"), TestKey.ENCODER, new RealDocumentSetup.GotView());
    Assert.assertEquals("{\"data\":{\"x\":125},\"seq\":7}", deA.datum.get(0).toString());
    Assert.assertEquals("{\"data\":{\"x\":126},\"seq\":10}", deB.datum.get(0).toString());
    setup.assertCompare();
  }

  @Test
  public void views_no_monitor() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @construct { x = 123; } @connected { x++; return true; }",
            null,
            false);
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    final var deNO_ONE = new RealDocumentSetup.ArrayPerspective();
    setup.document.createPrivateView(NtPrincipal.NO_ONE, deNO_ONE, new JsonStreamReader("{}"), TestKey.ENCODER, new RealDocumentSetup.GotView());
    Assert.assertEquals(1, deNO_ONE.datum.size());
    Assert.assertEquals("{\"data\":{\"x\":124},\"seq\":4}", deNO_ONE.datum.get(0).toString());
    final var deA = new RealDocumentSetup.ArrayPerspective();
    final var deB = new RealDocumentSetup.ArrayPerspective();
    setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(5));
    setup.document.createPrivateView(A, deA, new JsonStreamReader("{}"), TestKey.ENCODER, new RealDocumentSetup.GotView());
    setup.document.connect(ContextSupport.WRAP(B), new RealDocumentSetup.AssertInt(8));
    setup.document.createPrivateView(B, deB, new JsonStreamReader("{}"), TestKey.ENCODER, new RealDocumentSetup.GotView());
    Assert.assertEquals("{\"data\":{\"x\":125},\"seq\":7}", deA.datum.get(0).toString());
    Assert.assertEquals("{\"data\":{\"x\":126},\"seq\":10}", deB.datum.get(0).toString());
    setup.assertCompare();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void cant_connect_twice() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "public int x; @construct { x = 123; } @connected { x++; return true; }",
            null,
            false);
    Assert.assertEquals(
        123,
        ((int)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("x")));
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    Assert.assertEquals(
        124,
        ((int)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("x")));
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertFailure(115724));
    Assert.assertEquals(
        124,
        ((int)
            ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree())
                .get("x")));
    setup.assertCompare();
    Assert.assertEquals(0, setup.document.getCodeCost());
  }
}
