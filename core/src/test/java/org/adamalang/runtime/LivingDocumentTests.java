/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime;

import java.util.HashMap;

import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.exceptions.GoodwillExhaustedException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.ops.StdOutDocumentMonitor;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;
import org.junit.Assert;
import org.junit.Test;

public class LivingDocumentTests {
  private static NtClient A = new NtClient("A", "TEST");
  private static NtClient B = new NtClient("B", "TEST");
  private static HashMap<String, LivingDocumentFactory> compilerCache = new HashMap<>();

  public static LivingDocumentFactory compile(final String code) throws Exception {
    final var options = CompilerOptions.start().enableCodeCoverage().noCost().make();
    final var globals = GlobalObjectPool.createPoolWithStdLib();
    final var state = new EnvironmentState(globals, options);
    final var document = new Document();
    document.setClassName("MeCode");
    final var tokenEngine = new TokenEngine("<direct code>", code.codePoints().iterator());
    final var parser = new Parser(tokenEngine);
    parser.document().accept(document);
    if (!document.check(state)) {
      final var issues = Utility.createArrayNode();
      document.writeErrorsAsLanguageServerDiagnosticArray(issues);
      throw new Exception("Failed to check:" + issues.toPrettyString());
    }
    final var java = document.compileJava(state);
    var cached = compilerCache.get(java);
    if (cached == null) {
      cached = new LivingDocumentFactory("MeCode", java, "{}");
      compilerCache.put(java, cached);
    }
    return cached;
  }

  @Test
  public void bad_json() throws Exception {
    try {
      final var setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set[]> chan; #wait { foreach(x in chan.fetch(@no_one).await()) { t += x.v; }  }", "");
      Assert.fail();
    } catch (RuntimeException re) {
    }
  }

  @Test
  public void accept_array_message() throws Exception {
    final var setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set[]> chan; #wait { foreach(x in chan.fetch(@no_one).await()) { t += x.v; }  }");
    setup.document.connect(NtClient.NO_ONE, new RealDocumentSetup.AssertInt(3));
    Assert.assertEquals(0, (int) ((double)( (HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("t")));
    setup.document.send(NtClient.NO_ONE, "chan", "[{\"v\":1000},{\"v\":100000},{\"v\":1}]", new RealDocumentSetup.AssertInt(5));
    Assert.assertEquals(101001, (int) ((double)( (HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("t")));
  }

  @Test
  public void blocking_test() throws Exception {
    final var setup = new RealDocumentSetup("@construct { transition #foo; } #foo { block; } test ZOO { @step; assert @blocked; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals("TEST[ZOO] = 100.0%\n" + "...DUMP:{\"__blocked\":true}\n", report.toString());
  }

  @Test
  public void preempt() throws Exception {
    final var setup = new RealDocumentSetup("public int v; @construct { v = 1; transition #foo; } #foo { v = 2; preempt #zoo; block; } #zoo { v = 3; } ");
    Assert.assertEquals(3, (int) ((double)( (HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("v")));
  }


  @Test
  public void command_unknown() throws Exception {
    String json;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
      json = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", json);
    final var writer = setup.document.forge("nope", A);
    writer.endObject();
    try {
      setup.document.document.__transact(writer.toString());
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5009, drre.code);
    }
  }

  @Test
  public void construct_requirements_must_have_arg() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    final var document = setup.factory.create(new StdOutDocumentMonitor());
    final var writer = setup.document.forge("construct", A);
    writer.endObject();
    try {
      document.__transact(writer.toString());
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5013, drre.code);
    }
  }

  @Test
  public void futures_blocked() throws Exception {
    final var setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> chan; #wait { t = chan.fetch(@no_one).await().v; }");
    Assert.assertTrue((Boolean) ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("__blocked"));
  }

  @Test
  public void futures_blocked_still_blocked_wrong_user() throws Exception {
    final var setup = new RealDocumentSetup("@connected(who) { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> chan; #wait { t = chan.fetch(@no_one).await().v; }");
    Assert.assertTrue((Boolean) ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("__blocked"));
    setup.document.connect(A, new RealDocumentSetup.AssertInt(3));
    setup.document.send(A, "chan", "{\"v\":74}", new RealDocumentSetup.AssertInt(5));
    Assert.assertTrue((Boolean) ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("__blocked"));
    setup.assertCompare();
  }

  @Test
  public void futures_blocked_then_unblocked() throws Exception {
    final var setup = new RealDocumentSetup("@connected(who) { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> chan; #wait { t = chan.fetch(@no_one).await().v; }");
    Assert.assertTrue((Boolean) ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("__blocked"));
    setup.document.connect(NtClient.NO_ONE, new RealDocumentSetup.AssertInt(3));
    setup.document.send(NtClient.NO_ONE, "chan", "{\"v\":74}", new RealDocumentSetup.AssertInt(5));
    Assert.assertFalse((Boolean) ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("__blocked"));
    setup.assertCompare();
  }
  @Test
  public void futures_hydrate_missing_data() throws Exception {
    final var setup = new RealDocumentSetup(
            "@connected(who) { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> cha; channel<Set> chb; #wait { t = cha.fetch(@no_one).await().v; t += chb.fetch(@no_one).await().v; }", "{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__blocked_on\":\"cha\",\"__blocked\":true,\"__seq\":5,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__messages\":{\"0\":{\"nope\":true,\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"channel\":\"chb\",\"message\":{\"v\":50}}},\"__message_id\":1}");
    setup.document.send(NtClient.NO_ONE, "cha", "{\"v\":25}", new RealDocumentSetup.AssertInt(7));
    Assert.assertFalse((Boolean) ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("__blocked"));
    setup.assertCompare();
  }
    /*



  @Test
  public void futures_out_of_order_rehydrate() throws Exception {
    ObjectNode node;
    {
      final var setup = new RealDocumentSetup(
          "@connected(who) { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> cha; channel<Set> chb; #wait { t = cha.fetch(@no_one).await().v; t += chb.fetch(@no_one).await().v; }");
      setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
      Assert.assertEquals("{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__blocked_on\":\"cha\",\"__blocked\":true,\"__seq\":1}", setup.logger.node.toString());
      setup.drive(setup.transactor.connect(NtClient.NO_ONE));
      setup.drive(setup.transactor.send(NtClient.NO_ONE, "chb", "{\"v\":50}"));
      Assert.assertEquals(
          "{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__blocked_on\":\"cha\",\"__blocked\":true,\"__seq\":5,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__messages\":{\"0\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"channel\":\"chb\",\"timestamp\":\"0\",\"message\":{\"v\":50}}},\"__message_id\":1}",
          setup.logger.node.toString());
      node = setup.logger.node.deepCopy();
      setup.assertCompare();
    }
    final var setup = new RealDocumentSetup(
        "@connected(who) { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> cha; channel<Set> chb; #wait { t = cha.fetch(@no_one).await().v; t += chb.fetch(@no_one).await().v; }", node);
    setup.transactor.create();
    setup.transactor.insert(node.toString());
    setup.drive(setup.transactor.send(NtClient.NO_ONE, "cha", "{\"v\":25}"));
    Assert.assertEquals(
        "{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-5106534569952410475\",\"__blocked_on\":\"cha\",\"__blocked\":false,\"__seq\":7,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__message_id\":2,\"__auto_future_id\":2,\"t\":75}",
        setup.logger.node.toString());
    setup.assertCompare();
  }

  */

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
      final var setup = new RealDocumentSetup("@construct { transition #loop; } #loop { while(true) {} }");
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
  public void infinite_loop_bubble() throws Exception {
    var gotIt = false;
    try {
      final var setup = new RealDocumentSetup("@connected(who) { return true; } function inf() -> int { int z = 0; while (z < 10000000) { z++; } return z; } bubble<who> x = inf();");
      setup.document.connect(NtClient.NO_ONE, new RealDocumentSetup.AssertInt(3));
      setup.document.createPrivateView(NtClient.NO_ONE, Perspective.DEAD, new RealDocumentSetup.GotView());
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
      final var setup = new RealDocumentSetup("@connected(who) { return true; } function inf() -> int { int z = 0; while (z < 10000000) { z++; } return z; } bubble<who> x = inf();", null, false);
      setup.document.connect(NtClient.NO_ONE, new RealDocumentSetup.AssertInt(3));
      setup.document.createPrivateView(NtClient.NO_ONE, Perspective.DEAD, new RealDocumentSetup.GotView());
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
  public void invoke_random() throws Exception {
    final var setup = new RealDocumentSetup("double d1; double d2; int i1; int i2; long l; int z; @construct { d1 = Random.genDouble(); d2 = Random.getDoubleGaussian() * 6; i1 = Random.genInt(); i2 = Random.genBoundInt(50); l = Random.genLong(); z = Random.genBoundInt(-1); }");
    String d1 = ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("d1").toString();
    String d2 = ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("d2").toString();
    String i1 = ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("i1").toString();
    String i2 = ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("i2").toString();
    String l = ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("l").toString();

    Assert.assertEquals("0.7231742029971469", d1);
    Assert.assertEquals("2.6429286547789945", d2);
    Assert.assertEquals("-5.35098017E8", i1);
    Assert.assertEquals("26.0", i2);
    Assert.assertEquals("-5237980416576129062", l);
    setup.assertCompare();
  }

  @Test
  public void invoke_time() throws Exception {
    final var setup = new RealDocumentSetup("long x; @construct { x = Time.now(); }", null, true, new MockTime(450));
    setup.document.invalidate(new RealDocumentSetup.AssertInt(2));
    String x = ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("x").toString();
    String __time = ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("__time").toString();
    Assert.assertEquals("450", __time);
    Assert.assertEquals("450", x);
    setup.assertCompare();
  }

  @Test
  public void message_abort() throws Exception {
    final var setup = new RealDocumentSetup("public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { x = 100; abort; }");
    setup.document.connect(NtClient.NO_ONE, new RealDocumentSetup.AssertInt(3));
    setup.document.send(NtClient.NO_ONE, "foo", "{}", new RealDocumentSetup.AssertInt(6));
    String x = ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("x").toString();
    Assert.assertEquals("42.0", x);
    setup.assertCompare();
  }


  @Test
  public void test_invoke() throws Exception {
    final var setup = new RealDocumentSetup("int x = 0; #whoop { x = 123; } test ZOO { invoke #whoop; assert x == 123; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals("TEST[ZOO] = 100.0%\n" + "...DUMP:{\"x\":123}\n", report.toString());
    setup.assertCompare();
  }

  @Test
  public void test_invoke_no_monitor() throws Exception {
    final var setup = new RealDocumentSetup("int x = 0; #whoop { x = 123; } test ZOO { invoke #whoop; assert x == 123; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, null, "42");
    Assert.assertEquals("TEST[ZOO] = 100.0%\n" + "...DUMP:{\"x\":123}\n", report.toString());
    setup.assertCompare();
  }

  @Test
  public void pump_abort_rollback() throws Exception {
    final var setup = new RealDocumentSetup("message M { int z; } int x = 0; channel foo(M m) { x = m.z; abort; } test ZOO { @pump {z:123} into foo; @step; assert x == 0; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals("TEST[ZOO] = 100.0%\n", report.toString());
    setup.assertCompare();
  }

  @Test
  public void pump_test() throws Exception {
    final var setup = new RealDocumentSetup("message M { int z; } int x = 0; channel foo(M m) { x = m.z; } test ZOO { @pump {z:123} into foo; @step; assert x == 123; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals("TEST[ZOO] = 100.0%\n", report.toString());
    setup.assertCompare();
  }

  @Test
  public void run_tests() throws Exception {
    final var setup = new RealDocumentSetup("test FOO {} test GOO { assert true; } test ZOO { @step; assert false; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals("TEST[FOO] HAS NO ASSERTS\n" + "TEST[GOO] = 100.0%\n" + "TEST[ZOO] = 0.0% (HAS FAILURES)\n", report.toString());
    setup.assertCompare();
  }

  @Test
  public void send_must_be_connected() throws Exception {
    final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; } message M {} channel<M> foo;");
    setup.document.send(NtClient.NO_ONE, "foo", "{}", new RealDocumentSetup.AssertFailure());
    setup.document.connect(NtClient.NO_ONE, new RealDocumentSetup.AssertInt(3));
    setup.document.send(NtClient.NO_ONE, "foo", "{}", new RealDocumentSetup.AssertInt(5));
    setup.assertCompare();
  }

  @Test
  public void multi_views() throws Exception {
    final var setup = new RealDocumentSetup("public int x; @construct { x = 123; } @connected (who) { x++; return true; }");

    RealDocumentSetup.GotView pv1 = new RealDocumentSetup.GotView();
    RealDocumentSetup.GotView pv2 = new RealDocumentSetup.GotView();
    RealDocumentSetup.GotView pv3 = new RealDocumentSetup.GotView();

    Assert.assertFalse(setup.document.isConnected(NtClient.NO_ONE));
    RealDocumentSetup.ArrayPerspective viewOne = new RealDocumentSetup.ArrayPerspective();
    setup.document.connect(NtClient.NO_ONE, new RealDocumentSetup.AssertInt(3));
    setup.document.createPrivateView(NtClient.NO_ONE, viewOne, pv1);
    Assert.assertEquals(1, viewOne.datum.size());
    Assert.assertTrue(setup.document.isConnected(NtClient.NO_ONE));

    RealDocumentSetup.ArrayPerspective viewTwo = new RealDocumentSetup.ArrayPerspective();
    setup.document.createPrivateView(NtClient.NO_ONE, viewTwo, pv2);
    Assert.assertEquals(2, viewOne.datum.size());
    Assert.assertEquals(1, viewTwo.datum.size());

    RealDocumentSetup.ArrayPerspective viewThree = new RealDocumentSetup.ArrayPerspective();
    setup.document.createPrivateView(NtClient.NO_ONE, viewThree, pv3);
    Assert.assertEquals(3, viewOne.datum.size());
    Assert.assertEquals(2, viewTwo.datum.size());
    Assert.assertEquals(1, viewThree.datum.size());

    Assert.assertEquals(3, setup.document.garbageCollectPrivateViewsFor(NtClient.NO_ONE));
    pv1.view.kill();
    pv3.view.kill();
    Assert.assertEquals(1, setup.document.garbageCollectPrivateViewsFor(NtClient.NO_ONE));
    setup.document.invalidate(new RealDocumentSetup.AssertInt(7));
    pv2.view.kill();
    Assert.assertEquals(0, setup.document.garbageCollectPrivateViewsFor(NtClient.NO_ONE));
    setup.document.disconnect(NtClient.NO_ONE, new RealDocumentSetup.AssertInt(9));
    setup.assertCompare();
  }

  @Test
  public void send_requirements_must_have_channel() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
      setup.document.connect(A, new RealDocumentSetup.AssertInt(3));
      prior = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", prior);
    final var writer = setup.document.forge("send", A);
    writer.endObject();
    try {
      setup.document.document.__transact(writer.toString());
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5016, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void send_requirements_must_have_message() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
      setup.document.connect(A, new RealDocumentSetup.AssertInt(3));
      prior = setup.document.json();
    }
    final var setup = new RealDocumentSetup("@construct {}", prior);
    final var writer = setup.document.forge("send", A);
    writer.writeObjectFieldIntro("channel");
    writer.writeFastString("foo");
    writer.endObject();
    try {
      setup.document.document.__transact(writer.toString());
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5017, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void state_machine_progress() throws Exception {
    final var setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } @construct { transition #next; } int t = 0; #next { t++; if (t == 10) { transition #end; } else { transition #next; } } #end {}");
    String t = ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("t").toString();
    Assert.assertEquals("10.0", t);
  }

  @Test
  public void state_machine_progress_over_time() throws Exception {
    final var setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } @construct { transition #next; } int t = 0; #next { t++; if (t == 10) { transition #end; } else { transition #next in 0.25; } } #end {}");
    for (int k = 0; k < 26; k++) {
      setup.time.time += 100;
      setup.document.invalidate(new RealDocumentSetup.AssertInt(2 + k));
      String state = ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("__state").toString();
      System.err.println(state + "@" + k);
      Assert.assertEquals("next", state);
    }
    setup.document.invalidate(new RealDocumentSetup.AssertInt(28));
    setup.time.time += 100;
    setup.document.invalidate(new RealDocumentSetup.AssertInt(30));
    String t = ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("t").toString();
    Assert.assertEquals("10.0", t);
  }

  @Test
  public void transact() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    setup.assertCompare();
  }

  @Test
  public void transact_add_client_allowed() throws Exception {
    final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
    setup.document.connect(A, new RealDocumentSetup.AssertInt(3));
    setup.document.disconnect(A, new RealDocumentSetup.AssertInt(5));
    setup.assertCompare();
  }

  @Test
  public void transact_add_client_cant_connect_again_but_only_after_disconnect() throws Exception {
    final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
    setup.document.connect(A, new RealDocumentSetup.AssertInt(3));
    setup.document.connect(A, new RealDocumentSetup.AssertFailure());
    setup.document.disconnect(A, new RealDocumentSetup.AssertFailure());
    setup.document.connect(A, new RealDocumentSetup.AssertInt(7));
    setup.assertCompare();
  }

  @Test
  public void transact_add_client_not_allowed() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    setup.document.connect(A, new RealDocumentSetup.AssertFailure());
    setup.assertCompare();
  }

  @Test
  public void transact_disconnect_without_connection() throws Exception {
    final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
    setup.document.disconnect(A, new RealDocumentSetup.AssertFailure());
    setup.document.connect(A, new RealDocumentSetup.AssertInt(3));
    setup.document.disconnect(A, new RealDocumentSetup.AssertInt(5));
    setup.document.connect(A, new RealDocumentSetup.AssertInt(7));
    setup.document.disconnect(A, new RealDocumentSetup.AssertInt(9));
    setup.assertCompare();
  }

  @Test
  public void transact_double_construct_document() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
      setup.document.connect(A, new RealDocumentSetup.AssertInt(3));
      prior = setup.document.json();
      setup.assertCompare();
    }
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }", prior);
      final var document = setup.factory.create(new StdOutDocumentMonitor());
      document.__insert(new JsonStreamReader(prior.toString()));
      final var writer = setup.document.forge("construct", A);
      writer.writeObjectFieldIntro("arg");
      writer.beginObject();
      writer.endObject();
      writer.endObject();
      try {
        document.__transact(writer.toString());
        Assert.fail();
      } catch (final ErrorCodeException drre) {
        Assert.assertEquals(5012, drre.code);
      }
    }
  }

  @Test
  public void transact_hydrate_clients() throws Exception {
    String prior;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
      setup.document.connect(A, new RealDocumentSetup.AssertInt(3));
      prior = setup.document.json();
      setup.assertCompare();
    }
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }", prior);
      setup.document.connect(A, new RealDocumentSetup.AssertFailure());
      setup.document.connect(B, new RealDocumentSetup.AssertInt(5));
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
      document.__transact(writer.toString());
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5006, drre.code);
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
      document.__transact(writer.toString());
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5007, drre.code);
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
      document.__transact(writer.toString());
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5008, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void views() throws Exception {
    final var setup = new RealDocumentSetup("public int x; @construct { x = 123; } @connected (who) { x++; return true; }");
    setup.document.connect(NtClient.NO_ONE, new RealDocumentSetup.AssertInt(3));
    final var deNO_ONE = new RealDocumentSetup.ArrayPerspective();
    setup.document.createPrivateView(NtClient.NO_ONE, deNO_ONE, new RealDocumentSetup.GotView());
    Assert.assertEquals(1, deNO_ONE.datum.size());
    Assert.assertEquals("{\"data\":{\"x\":124},\"outstanding\":[],\"blockers\":[],\"seq\":4}", deNO_ONE.datum.get(0).toString());
    final var deA = new RealDocumentSetup.ArrayPerspective();
    final var deB = new RealDocumentSetup.ArrayPerspective();
    setup.document.connect(A, new RealDocumentSetup.AssertInt(6));
    setup.document.createPrivateView(A, deA, new RealDocumentSetup.GotView());
    setup.document.connect(B, new RealDocumentSetup.AssertInt(9));
    setup.document.createPrivateView(B, deB, new RealDocumentSetup.GotView());
    Assert.assertEquals("{\"data\":{\"x\":125},\"outstanding\":[],\"blockers\":[],\"seq\":7}", deA.datum.get(0).toString());
    Assert.assertEquals("{\"data\":{\"x\":126},\"outstanding\":[],\"blockers\":[],\"seq\":10}", deB.datum.get(0).toString());
    setup.assertCompare();
  }

  @Test
  public void views_no_monitor() throws Exception {
    final var setup = new RealDocumentSetup("public int x; @construct { x = 123; } @connected (who) { x++; return true; }", null, false);
    setup.document.connect(NtClient.NO_ONE, new RealDocumentSetup.AssertInt(3));
    final var deNO_ONE = new RealDocumentSetup.ArrayPerspective();
    setup.document.createPrivateView(NtClient.NO_ONE, deNO_ONE, new RealDocumentSetup.GotView());
    Assert.assertEquals(1, deNO_ONE.datum.size());
    Assert.assertEquals("{\"data\":{\"x\":124},\"outstanding\":[],\"blockers\":[],\"seq\":4}", deNO_ONE.datum.get(0).toString());
    final var deA = new RealDocumentSetup.ArrayPerspective();
    final var deB = new RealDocumentSetup.ArrayPerspective();
    setup.document.connect(A, new RealDocumentSetup.AssertInt(6));
    setup.document.createPrivateView(A, deA, new RealDocumentSetup.GotView());
    setup.document.connect(B, new RealDocumentSetup.AssertInt(9));
    setup.document.createPrivateView(B, deB, new RealDocumentSetup.GotView());
    Assert.assertEquals("{\"data\":{\"x\":125},\"outstanding\":[],\"blockers\":[],\"seq\":7}", deA.datum.get(0).toString());
    Assert.assertEquals("{\"data\":{\"x\":126},\"outstanding\":[],\"blockers\":[],\"seq\":10}", deB.datum.get(0).toString());
    setup.assertCompare();
  }

  @Test
  public void cant_connect_twice() throws Exception {
    final var setup = new RealDocumentSetup("public int x; @construct { x = 123; } @connected (who) { x++; return true; }", null, false);
    Assert.assertEquals(123, (int) ((double)( (HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("x")));
    setup.document.connect(NtClient.NO_ONE, new RealDocumentSetup.AssertInt(3));
    Assert.assertEquals(124, (int) ((double)( (HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("x")));
    setup.document.connect(NtClient.NO_ONE, new RealDocumentSetup.AssertFailure());
    Assert.assertEquals(124, (int) ((double) ((HashMap<String, Object>) new JsonStreamReader(setup.document.json()).readJavaTree()).get("x")));
    setup.assertCompare();
    Assert.assertEquals(0, setup.document.getCodeCost());
  }
}