/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.exceptions.GoodwillExhaustedException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
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
import com.fasterxml.jackson.databind.node.ObjectNode;

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
  public void accept_array_message() throws Exception {
    final var setup = new RealDocumentSetup(
        "@connected(who) { return who == @no_one; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set[]> chan; #wait { foreach(x in chan.fetch(@no_one).await()) { t += x.v; }  }");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    Assert.assertEquals("{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__blocked_on\":\"chan\",\"__blocked\":true,\"__seq\":1}", setup.logger.node.toString());
    setup.drive(setup.transactor.connect(NtClient.NO_ONE));
    setup.drive(setup.transactor.send(NtClient.NO_ONE, "chan", "[{\"v\":1000},{\"v\":100000},{\"v\":1}]"));
    Assert.assertEquals(
        "{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-5106534569952410475\",\"__blocked_on\":\"chan\",\"__blocked\":false,\"__seq\":5,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__message_id\":1,\"__auto_future_id\":1,\"t\":101001}",
        setup.logger.node.toString());
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
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "42"));
    setup.drive(setup.transactor.invalidate());
    Assert.assertEquals("{\"v\":3,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__blocked\":false,\"__seq\":3,\"__entropy\":\"-2768345660179580053\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"0\"}", setup.transactor.json());
  }

  @Test
  public void command_unknown() throws Exception {
    ObjectNode state;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
      setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
      state = setup.logger.node;
    }
    final var setup = new RealDocumentSetup("@construct {}");
    final var document = setup.factory.create(new StdOutDocumentMonitor());
    document.__insert(new JsonStreamReader(state.toString()));
    final var writer = setup.transactor.forge("nope", A);
    writer.endObject();
    try {
      document.__transact(writer.toString());
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5009, drre.code);
    }
  }

  @Test
  public void construct_requirements_must_have_arg() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    final var document = setup.factory.create(new StdOutDocumentMonitor());
    final var writer = setup.transactor.forge("construct", A);
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
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    Assert.assertEquals("{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__blocked_on\":\"chan\",\"__blocked\":true,\"__seq\":1}", setup.logger.node.toString());
  }

  @Test
  public void futures_blocked_still_blocked_wrong_user() throws Exception {
    final var setup = new RealDocumentSetup("@connected(who) { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> chan; #wait { t = chan.fetch(@no_one).await().v; }");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    Assert.assertEquals("{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__blocked_on\":\"chan\",\"__blocked\":true,\"__seq\":1}", setup.logger.node.toString());
    setup.drive(setup.transactor.connect(A));
    setup.drive(setup.transactor.send(A, "chan", "{\"v\":74}"));
    Assert.assertEquals(
        "{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__blocked_on\":\"chan\",\"__blocked\":true,\"__seq\":5,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"A\",\"authority\":\"TEST\"}},\"__messages\":{\"0\":{\"who\":{\"agent\":\"A\",\"authority\":\"TEST\"},\"channel\":\"chan\",\"timestamp\":\"0\",\"message\":{\"v\":74}}},\"__message_id\":1}",
        setup.logger.node.toString());
    setup.assertCompare();
  }

  @Test
  public void futures_blocked_then_unblocked() throws Exception {
    final var setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> chan; #wait { t = chan.fetch(@no_one).await().v; }");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    Assert.assertEquals("{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__blocked_on\":\"chan\",\"__blocked\":true,\"__seq\":1}", setup.logger.node.toString());
    setup.drive(setup.transactor.connect(NtClient.NO_ONE));
    setup.drive(setup.transactor.send(NtClient.NO_ONE, "chan", "{\"v\":74}"));
    Assert.assertEquals(
        "{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-5106534569952410475\",\"__blocked_on\":\"chan\",\"__blocked\":false,\"__seq\":5,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__message_id\":1,\"__auto_future_id\":1,\"t\":74}",
        setup.logger.node.toString());
    setup.assertCompare();
  }

  @Test
  public void futures_hydrate_missing_data() throws Exception {
    final var setup = new RealDocumentSetup(
        "@connected(who) { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> cha; channel<Set> chb; #wait { t = cha.fetch(@no_one).await().v; t += chb.fetch(@no_one).await().v; }");
    setup.transactor.create();
    setup.transactor.insert(
        "{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__blocked_on\":\"cha\",\"__blocked\":true,\"__seq\":5,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__messages\":{\"0\":{\"nope\":true,\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"channel\":\"chb\",\"message\":{\"v\":50}}},\"__message_id\":1}");
    setup.mirror.insert("{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__blocked_on\":\"cha\",\"__blocked\":true,\"__seq\":5,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__messages\":{\"0\":{\"nope\":true,\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"channel\":\"chb\",\"message\":{\"v\":50}}},\"__message_id\":1}");
    setup.drive(setup.transactor.send(NtClient.NO_ONE, "cha", "{\"v\":25}"));
    Assert.assertEquals("{\"__seq\":7,\"__message_id\":2,\"__state\":\"\",\"__blocked\":false,\"__entropy\":\"-5106534569952410475\",\"__auto_future_id\":2,\"t\":75}", setup.logger.node.toString());
    setup.assertCompare();
  }

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

  @Test
  public void infinite_loop_1() throws Exception {
    var gotIt = false;
    try {
      final var setup = new RealDocumentSetup("@construct { while(true) {} }");
      setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
      Assert.fail();
    } catch (final GoodwillExhaustedException yay) {
      yay.printStackTrace();
      gotIt = true;
    }
    Assert.assertTrue(gotIt);
  }

  @Test
  public void infinite_loop_2() throws Exception {
    var gotIt = false;
    final var setup = new RealDocumentSetup("@construct { transition #loop; } #loop { while(true) {} }");
    try {
      setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
      Assert.fail();
    } catch (final GoodwillExhaustedException yay) {
      yay.printStackTrace();
      gotIt = true;
    }
    Assert.assertTrue(gotIt);
    Assert.assertEquals(0, setup.transactor.getCodeCost());
    setup.transactor.bill();
    Assert.assertEquals("{\"__state\":\"loop\",\"__constructed\":true,\"__entropy\":\"123\",\"__goodwill_used\":100000,\"__cost\":0,\"__billing_seq\":0}", setup.logger.node.toString());
  }

  @Test
  public void infinite_loop_bubble() throws Exception {
    var gotIt = false;
    final var setup = new RealDocumentSetup("@connected(who) { return true; } function inf() -> int { int z = 0; while (z < 1000000) { z++; } return z; } bubble<who> x = inf();");
    try {
      setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
      setup.transactor.createView(A, str -> {});
      setup.drive(setup.transactor.connect(A));
      Assert.fail();
    } catch (final GoodwillExhaustedException yay) {
      yay.printStackTrace();
      gotIt = true;
    }
    Assert.assertEquals(0, setup.transactor.getCodeCost());
    Assert.assertTrue(gotIt);
  }

  @Test
  public void invoke_random() throws Exception {
    final var setup = new RealDocumentSetup(
        "double d1; double d2; int i1; int i2; long l; int z; @construct { d1 = Random.genDouble(); d2 = Random.getDoubleGaussian() * 6; i1 = Random.genInt(); i2 = Random.genBoundInt(50); l = Random.genLong(); z = Random.genBoundInt(-1); }");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    Assert.assertEquals("{\"__constructed\":true,\"__entropy\":\"-5106534569952410475\",\"d1\":0.7231742029971469,\"d2\":2.6429286547789945,\"i1\":-535098017,\"i2\":26,\"l\":\"-5237980416576129062\",\"__seq\":1}",
        setup.logger.node.toString());
  }

  @Test
  public void invoke_time() throws Exception {
    final var setup = new RealDocumentSetup("long x; @construct { x = Time.now(); }");
    setup.time.time = 450;
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    Assert.assertEquals("{\"__constructed\":true,\"__entropy\":\"-5106534569952410475\",\"__time\":\"450\",\"x\":\"450\",\"__seq\":1}", setup.logger.node.toString());
  }

  @Test
  public void message_abort() throws Exception {
    final var setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } message M {} channel foo(M x) { abort; }");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    setup.drive(setup.transactor.connect(NtClient.NO_ONE));
    Assert.assertEquals("{\"__constructed\":true,\"__entropy\":\"786253046697430328\",\"__seq\":3,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}",
        setup.logger.node.toString());
    setup.drive(setup.transactor.send(NtClient.NO_ONE, "foo", "{}"));
    Assert.assertEquals("{\"__constructed\":true,\"__entropy\":\"8270396388693936851\",\"__seq\":6,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__message_id\":1}",
        setup.logger.node.toString());
  }

  @Test
  public void multi_views() throws Exception {
    final var setup = new RealDocumentSetup("public int x; @construct { x = 123; } @connected (who) { x++; return true; }");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    Assert.assertFalse(setup.transactor.isConnected(NtClient.NO_ONE));
    final var viewOneData = new ArrayList<ObjectNode>();
    final var viewOne = setup.transactor.createView(NtClient.NO_ONE, str -> {
      viewOneData.add(Utility.parseJsonObject(str));
    });
    setup.drive(setup.transactor.connect(NtClient.NO_ONE));
    Assert.assertEquals(1, viewOneData.size());
    Assert.assertEquals("{\"data\":{\"x\":124},\"outstanding\":[],\"blockers\":[],\"seq\":3}", viewOneData.get(0).toString());
    Assert.assertTrue(setup.transactor.isConnected(NtClient.NO_ONE));
    final var viewTwoData = new ArrayList<ObjectNode>();
    final var viewTwo = setup.transactor.createView(NtClient.NO_ONE, str -> {
      viewTwoData.add(Utility.parseJsonObject(str));
    });
    setup.transactor.drive();
    Assert.assertEquals(2, viewOneData.size());
    Assert.assertEquals(1, viewTwoData.size());
    Assert.assertEquals("{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":4}", viewOneData.get(1).toString());
    Assert.assertEquals("{\"data\":{\"x\":124},\"outstanding\":[],\"blockers\":[],\"seq\":4}", viewTwoData.get(0).toString());
    final var viewThreeData = new ArrayList<ObjectNode>();
    final var viewThree = setup.transactor.createView(NtClient.NO_ONE, str -> {
      viewThreeData.add(Utility.parseJsonObject(str));
    });
    setup.transactor.drive();
    Assert.assertEquals(3, viewOneData.size());
    Assert.assertEquals(2, viewTwoData.size());
    Assert.assertEquals(1, viewThreeData.size());
    Assert.assertEquals("{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":5}", viewOneData.get(2).toString());
    Assert.assertEquals("{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":5}", viewTwoData.get(1).toString());
    Assert.assertEquals("{\"data\":{\"x\":124},\"outstanding\":[],\"blockers\":[],\"seq\":5}", viewThreeData.get(0).toString());
    viewOne.kill();
    viewThree.kill();
    Assert.assertEquals(1, setup.transactor.gcViewsFor(NtClient.NO_ONE));
    setup.transactor.drive();
    Assert.assertEquals(3, viewOneData.size());
    Assert.assertEquals(3, viewTwoData.size());
    Assert.assertEquals(1, viewThreeData.size());
    Assert.assertEquals("{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":6}", viewTwoData.get(2).toString());
    viewTwo.kill();
    Assert.assertEquals(0, setup.transactor.gcViewsFor(NtClient.NO_ONE));
    Assert.assertEquals(0, setup.transactor.gcViewsFor(NtClient.NO_ONE));
  }

  @Test
  public void pump_abort_rollback() throws Exception {
    final var setup = new RealDocumentSetup("message M { int z; } int x = 0; channel foo(M m) { x = m.z; abort; } test ZOO { @pump {z:123} into foo; @step; assert x == 0; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals("TEST[ZOO] = 100.0%\n", report.toString());
  }

  @Test
  public void pump_test() throws Exception {
    final var setup = new RealDocumentSetup("message M { int z; } int x = 0; channel foo(M m) { x = m.z; } test ZOO { @pump {z:123} into foo; @step; assert x == 123; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals("TEST[ZOO] = 100.0%\n", report.toString());
  }

  @Test
  public void run_tests() throws Exception {
    final var setup = new RealDocumentSetup("test FOO {} test GOO { assert true; } test ZOO { @step; assert false; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals("TEST[FOO] HAS NO ASSERTS\n" + "TEST[GOO] = 100.0%\n" + "TEST[ZOO] = 0.0% (HAS FAILURES)\n", report.toString());
  }

  @Test
  public void send_must_be_connected() throws Exception {
    final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; } message M {} channel<M> foo;");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    setup.assertInitial();
    try {
      setup.drive(setup.transactor.send(A, "foo", "{}"));
      Assert.fail();
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5015, drre.code);
    }
    setup.drive(setup.transactor.connect(A));
    setup.drive(setup.transactor.send(A, "foo", "{}"));
  }

  @Test
  public void send_requirements_must_have_channel() throws Exception {
    ObjectNode state;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
      setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
      setup.drive(setup.transactor.connect(A));
      state = setup.logger.node;
    }
    final var setup = new RealDocumentSetup("@construct {}");
    final var document = setup.factory.create(new StdOutDocumentMonitor());
    document.__insert(new JsonStreamReader(state.toString()));
    final var writer = setup.transactor.forge("send", A);
    writer.endObject();
    try {
      document.__transact(writer.toString());
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5016, drre.code);
    }
  }

  @Test
  public void send_requirements_must_have_message() throws Exception {
    ObjectNode state;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
      setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
      setup.drive(setup.transactor.connect(A));
      state = setup.logger.node;
    }
    final var setup = new RealDocumentSetup("@construct {}");
    final var document = setup.factory.create(new StdOutDocumentMonitor());
    document.__insert(new JsonStreamReader(state.toString()));
    final var writer = setup.transactor.forge("send", A);
    writer.writeObjectFieldIntro("channel");
    writer.writeFastString("foo");
    writer.endObject();
    try {
      document.__transact(writer.toString());
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5017, drre.code);
    }
  }

  @Test
  public void state_machine_progress() throws Exception {
    final var setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } @construct { transition #next; } int t = 0; #next { t++; if (t == 10) { transition #end; } else { transition #next; } } #end {}");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    Assert.assertEquals("{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-3139549461559497096\",\"__seq\":11,\"t\":10}", setup.logger.node.toString());
  }

  @Test
  public void state_machine_progress_over_time() throws Exception {
    final var setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } @construct { transition #next; } int t = 0; #next { t++; if (t == 10) { transition #end; } else { transition #next in 0.25; } } #end {}");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    Assert.assertEquals("{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-3139549461559497096\",\"__next_time\":\"2250\",\"__seq\":11,\"t\":10,\"__time\":\"2250\"}",
        setup.logger.node.toString());
  }

  @Test
  public void test_invoke() throws Exception {
    final var setup = new RealDocumentSetup("int x = 0; #whoop { x = 123; } test ZOO { invoke #whoop; assert x == 123; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, new StdOutDocumentMonitor(), "42");
    Assert.assertEquals("TEST[ZOO] = 100.0%\n" + "...DUMP:{\"x\":123}\n", report.toString());
  }

  @Test
  public void test_invoke_no_monitor() throws Exception {
    final var setup = new RealDocumentSetup("int x = 0; #whoop { x = 123; } test ZOO { invoke #whoop; assert x == 123; }");
    final var report = new TestReportBuilder();
    setup.factory.populateTestReport(report, null, "42");
    Assert.assertEquals("TEST[ZOO] = 100.0%\n" + "...DUMP:{\"x\":123}\n", report.toString());
  }

  @Test
  public void transact() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    setup.assertInitial();
    setup.transactor.close();
  }

  @Test
  public void transact_add_client_allowed() throws Exception {
    final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    setup.assertInitial();
    setup.drive(setup.transactor.connect(A));
    setup.transactor.disconnect(A);
  }

  @Test
  public void transact_add_client_cant_connect_again_but_only_after_disconnect() throws Exception {
    final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    setup.assertInitial();
    setup.drive(setup.transactor.connect(A));
    try {
      setup.drive(setup.transactor.connect(A));
      Assert.fail();
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5010, drre.code);
    }
    setup.transactor.disconnect(A);
    setup.transactor.drive();
    setup.drive(setup.transactor.connect(A));
    setup.assertCompare();
  }

  @Test
  public void transact_add_client_not_allowed() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    setup.assertInitial();
    try {
      setup.drive(setup.transactor.connect(A));
      Assert.fail();
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5011, drre.code);
    }
    setup.assertInitial();
    setup.assertCompare();
  }

  @Test
  public void transact_disconnect_without_connection() throws Exception {
    final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    setup.assertInitial();
    setup.transactor.disconnect(A);
    setup.drive(setup.transactor.connect(A));
    setup.transactor.disconnect(A);
    setup.drive(setup.transactor.connect(A));
    setup.transactor.disconnect(A);
    setup.assertCompare();
  }

  @Test
  public void transact_double_construct_document() throws Exception {
    ObjectNode prior = null;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
      setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
      setup.assertInitial();
      setup.drive(setup.transactor.connect(A));
      prior = setup.logger.node.deepCopy();
      setup.assertCompare();
    }
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
      final var document = setup.factory.create(new StdOutDocumentMonitor());
      document.__insert(new JsonStreamReader(prior.toString()));
      final var writer = setup.transactor.forge("construct", A);
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
  public void transact_double_construct_transactor() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    setup.assertInitial();
    try {
      setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5001, drre.code);
    }
    setup.assertCompare();
  }

  @Test
  public void transact_hydrate_clients() throws Exception {
    ObjectNode prior = null;
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
      setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
      setup.assertInitial();
      setup.drive(setup.transactor.connect(A));
      prior = setup.logger.node.deepCopy();
      setup.assertCompare();
    }
    {
      final var setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }", prior);
      setup.transactor.create();
      setup.transactor.insert(prior.toString());
      try {
        setup.drive(setup.transactor.connect(A));
        Assert.fail();
      } catch (final ErrorCodeException drre) {
        Assert.assertEquals(5010, drre.code);
      }
      setup.drive(setup.transactor.connect(B));
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
  }

  @Test
  public void transact_requirements_must_have_who_for_construct() throws Exception {
    final var setup = new RealDocumentSetup("@construct {}");
    final var document = setup.factory.create(new StdOutDocumentMonitor());
    final var writer = setup.transactor.forge("construct", null);
    writer.endObject();
    try {
      document.__transact(writer.toString());
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(5008, drre.code);
    }
  }

  @Test
  public void views() throws Exception {
    final var setup = new RealDocumentSetup("public int x; @construct { x = 123; } @connected (who) { x++; return true; }");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    final var deNO_ONE = new ArrayList<ObjectNode>();
    setup.transactor.createView(NtClient.NO_ONE, str -> {
      deNO_ONE.add(Utility.parseJsonObject(str));
    });
    setup.drive(setup.transactor.connect(NtClient.NO_ONE));
    Assert.assertEquals(1, deNO_ONE.size());
    Assert.assertEquals("{\"data\":{\"x\":124},\"outstanding\":[],\"blockers\":[],\"seq\":3}", deNO_ONE.get(0).toString());
    final var deA = new ArrayList<ObjectNode>();
    final var deB = new ArrayList<ObjectNode>();
    setup.transactor.createView(A, str -> deA.add(Utility.parseJsonObject(str)));
    setup.drive(setup.transactor.connect(A));
    Assert.assertEquals("{\"data\":{\"x\":125},\"outstanding\":[],\"blockers\":[],\"seq\":5}", deA.get(0).toString());
    setup.transactor.createView(B, str -> deB.add(Utility.parseJsonObject(str)));
    setup.drive(setup.transactor.connect(B));
    Assert.assertEquals("{\"data\":{\"x\":126},\"outstanding\":[],\"blockers\":[],\"seq\":7}", deB.get(0).toString());
    setup.assertCompare();
  }

  @Test
  public void views_no_monitor() throws Exception {
    final var setup = new RealDocumentSetup("public int x; @construct { x = 123; } @connected (who) { x++; return true; }", null, false);
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    final var deNO_ONE = new ArrayList<ObjectNode>();
    setup.transactor.createView(NtClient.NO_ONE, str -> {
      deNO_ONE.add(Utility.parseJsonObject(str));
    });
    setup.drive(setup.transactor.connect(NtClient.NO_ONE));
    Assert.assertEquals(1, deNO_ONE.size());
    Assert.assertEquals("{\"data\":{\"x\":124},\"outstanding\":[],\"blockers\":[],\"seq\":3}", deNO_ONE.get(0).toString());
    final var deA = new ArrayList<ObjectNode>();
    final var deB = new ArrayList<ObjectNode>();
    setup.transactor.createView(A, str -> deA.add(Utility.parseJsonObject(str)));
    setup.drive(setup.transactor.connect(A));
    Assert.assertEquals("{\"data\":{\"x\":125},\"outstanding\":[],\"blockers\":[],\"seq\":5}", deA.get(0).toString());
    setup.transactor.createView(B, str -> deB.add(Utility.parseJsonObject(str)));
    setup.drive(setup.transactor.connect(B));
    Assert.assertEquals("{\"data\":{\"x\":126},\"outstanding\":[],\"blockers\":[],\"seq\":7}", deB.get(0).toString());
    setup.assertCompare();
  }
}