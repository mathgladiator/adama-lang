/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.exceptions.DocumentRequestRejectedException;
import org.adamalang.runtime.exceptions.DocumentRequestRejectedReason;
import org.adamalang.runtime.exceptions.GoodwillExhaustedException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.ops.StdOutDocumentMonitor;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class LivingDocumentTests {
    private static NtClient A = new NtClient("A", "TEST");
    private static NtClient B = new NtClient("B", "TEST");
    private static HashMap<String, LivingDocumentFactory> compilerCache = new HashMap<>();

    public static LivingDocumentFactory compile(String code) throws Exception {
        CompilerOptions options = CompilerOptions.start().enableCodeCoverage().noCost().make();
        GlobalObjectPool globals = GlobalObjectPool.createPoolWithStdLib();
        EnvironmentState state = new EnvironmentState(globals, options);
        Document document = new Document();
        document.setClassName("MeCode");
        final var tokenEngine = new TokenEngine("<direct code>", code.codePoints().iterator());
        final var parser = new Parser(tokenEngine);
        parser.document().accept(document);
        if (!document.check(state)) {
            ArrayNode issues = Utility.createArrayNode();
            document.writeErrorsAsLanguageServerDiagnosticArray(issues);
            throw new Exception("Failed to check:" + issues.toPrettyString());
        }
        String java = document.compileJava(state);
        LivingDocumentFactory cached = compilerCache.get(java);
        if (cached == null) {
            cached = new LivingDocumentFactory("MeCode", java);
            compilerCache.put(java, cached);
        }
        return cached;
    }

    @Test
    public void transact() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("@construct {}");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        setup.assertInitial();
        setup.transactor.close();
    }

    @Test
    public void construct_requirements_must_have_arg() throws Exception{
        RealDocumentSetup setup = new RealDocumentSetup("@construct {}");
        LivingDocument document = setup.factory.create(Utility.createObjectNode(), new StdOutDocumentMonitor());
        ObjectNode request = setup.transactor.forge("construct", A);
        try {
            document.__transact(request);
        } catch (DocumentRequestRejectedException drre) {
            Assert.assertEquals(DocumentRequestRejectedReason.NoConstructorArg, drre.reason);
        }
    }

    @Test
    public void transact_requirements_must_have_command() throws Exception{
        RealDocumentSetup setup = new RealDocumentSetup("@construct {}");
        LivingDocument document = setup.factory.create(Utility.createObjectNode(), new StdOutDocumentMonitor());
        ObjectNode request = setup.transactor.forge("construct", A);
        request.remove("command");
        try {
            document.__transact(request);
        } catch (DocumentRequestRejectedException drre) {
            Assert.assertEquals(DocumentRequestRejectedReason.NoRequestCommand, drre.reason);
        }
    }

    @Test
    public void transact_requirements_must_have_timestamp() throws Exception{
        RealDocumentSetup setup = new RealDocumentSetup("@construct {}");
        LivingDocument document = setup.factory.create(Utility.createObjectNode(), new StdOutDocumentMonitor());
        ObjectNode request = setup.transactor.forge("construct", A);
        request.remove("timestamp");
        try {
            document.__transact(request);
        } catch (DocumentRequestRejectedException drre) {
            Assert.assertEquals(DocumentRequestRejectedReason.NoRequestTimestamp, drre.reason);
        }
    }

    @Test
    public void transact_requirements_must_have_who_for_construct() throws Exception{
        RealDocumentSetup setup = new RealDocumentSetup("@construct {}");
        LivingDocument document = setup.factory.create(Utility.createObjectNode(), new StdOutDocumentMonitor());
        ObjectNode request = setup.transactor.forge("construct", A);
        request.remove("who");
        try {
            document.__transact(request);
        } catch (DocumentRequestRejectedException drre) {
            Assert.assertEquals(DocumentRequestRejectedReason.NoRequestWho, drre.reason);
        }
    }

    @Test
    public void transact_double_construct_transactor() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("@construct {}");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        setup.assertInitial();
        try {
            setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        } catch (DocumentRequestRejectedException drre) {
            Assert.assertEquals(DocumentRequestRejectedReason.AlreadyConstructed, drre.reason);
        }
    }

    @Test
    public void transact_double_construct_document() throws Exception {
        ObjectNode prior = null;
        {
            RealDocumentSetup setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
            setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
            setup.assertInitial();
            setup.drive(setup.transactor.connect(A));
            prior = setup.logger.node.deepCopy();
        }
        {
            RealDocumentSetup setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
            LivingDocument document = setup.factory.create(prior, new StdOutDocumentMonitor());
            ObjectNode request = setup.transactor.forge("construct", A);
            request.set("arg", Utility.createObjectNode());
            try {
                document.__transact(request);
                Assert.fail();
            } catch (DocumentRequestRejectedException drre) {
                Assert.assertEquals(DocumentRequestRejectedReason.AlreadyConstructed, drre.reason);
            }
        }
    }

    @Test
    public void transact_add_client_not_allowed() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("@construct {}");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        setup.assertInitial();
        try {
            setup.drive(setup.transactor.connect(A));
            Assert.fail();
        } catch (DocumentRequestRejectedException drre) {
            Assert.assertEquals(DocumentRequestRejectedReason.ClientConnectRejected, drre.reason);
        }
        setup.assertInitial();
    }

    @Test
    public void transact_add_client_allowed() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        setup.assertInitial();
        setup.drive(setup.transactor.connect(A));
        setup.drive(setup.transactor.disconnect(A));
    }

    @Test
    public void transact_add_client_cant_connect_again_but_only_after_disconnect() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        setup.assertInitial();
        setup.drive(setup.transactor.connect(A));
        try {
            setup.drive(setup.transactor.connect(A));
            Assert.fail();
        } catch (DocumentRequestRejectedException drre) {
            Assert.assertEquals(DocumentRequestRejectedReason.ClientAlreadyConnected, drre.reason);
        }
        setup.drive(setup.transactor.disconnect(A));
        setup.drive(setup.transactor.connect(A));
    }

    @Test
    public void transact_disconnect_without_connection() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        setup.assertInitial();
        try {
            setup.drive(setup.transactor.disconnect(A));
            Assert.fail();
        } catch (DocumentRequestRejectedException drre) {
            Assert.assertEquals(DocumentRequestRejectedReason.ClientNotAlreadyConnected, drre.reason);
        }
        setup.drive(setup.transactor.connect(A));
        setup.drive(setup.transactor.disconnect(A));
    }

    @Test
    public void transact_hydrate_clients() throws Exception {
        ObjectNode prior = null;
        {
            RealDocumentSetup setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
            setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
            setup.assertInitial();
            setup.drive(setup.transactor.connect(A));
            prior = setup.logger.node.deepCopy();
        }
        {
            RealDocumentSetup setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }", prior);
            setup.transactor.seed(prior);
            try {
                setup.drive(setup.transactor.connect(A));
                Assert.fail();
            } catch (DocumentRequestRejectedException drre) {
                Assert.assertEquals(DocumentRequestRejectedReason.ClientAlreadyConnected, drre.reason);
            }
            setup.drive(setup.transactor.connect(B));
        }
    }

    @Test
    public void send_must_be_connected() throws Exception{
        RealDocumentSetup setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        setup.assertInitial();
        try {
            setup.drive(setup.transactor.send(A, "foo", Utility.createObjectNode()));
            Assert.fail();
        } catch (DocumentRequestRejectedException drre) {
            Assert.assertEquals(DocumentRequestRejectedReason.ClientNotConnectedForSend, drre.reason);
        }
        setup.drive(setup.transactor.connect(A));
        setup.drive(setup.transactor.send(A, "foo", Utility.createObjectNode()));
    }

    @Test
    public void send_requirements_must_have_channel() throws Exception {
        ObjectNode state;
        {
            RealDocumentSetup setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
            setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
            setup.drive(setup.transactor.connect(A));
            state = setup.logger.node;
        }
        RealDocumentSetup setup = new RealDocumentSetup("@construct {}");
        LivingDocument document = setup.factory.create(state, new StdOutDocumentMonitor());
        ObjectNode request = setup.transactor.forge("send", A);
        try {
            document.__transact(request);
        } catch (DocumentRequestRejectedException drre) {
            Assert.assertEquals(DocumentRequestRejectedReason.SendHasNoChannel, drre.reason);
        }
    }

    @Test
    public void send_requirements_must_have_message() throws Exception {
        ObjectNode state;
        {
            RealDocumentSetup setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
            setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
            setup.drive(setup.transactor.connect(A));
            state = setup.logger.node;
        }
        RealDocumentSetup setup = new RealDocumentSetup("@construct {}");
        LivingDocument document = setup.factory.create(state, new StdOutDocumentMonitor());
        ObjectNode request = setup.transactor.forge("send", A);
        request.put("channel", "foo");
        try {
            document.__transact(request);
        } catch (DocumentRequestRejectedException drre) {
            Assert.assertEquals(DocumentRequestRejectedReason.SendHasNoMessage, drre.reason);
        }
    }

    @Test
    public void state_machine_progress() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } @construct { transition #next; } int t = 0; #next { t++; if (t == 10) { transition #end; } else { transition #next; } } #end {}");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        Assert.assertEquals("{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-3139549461559497096\",\"__seedUsed\":\"865209291055887116\",\"__seq\":11,\"t\":10}", setup.logger.node.toString());
    }

    @Test
    public void state_machine_progress_over_time() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } @construct { transition #next; } int t = 0; #next { t++; if (t == 10) { transition #end; } else { transition #next in 0.25; } } #end {}");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        Assert.assertEquals("{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-3139549461559497096\",\"__seedUsed\":\"865209291055887116\",\"__next_time\":\"2250\",\"__seq\":11,\"t\":10,\"__time\":\"2250\"}", setup.logger.node.toString());
    }

    @Test
    public void futures_blocked() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> chan; #wait { t = chan.fetch(@no_one).await().v; }");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        Assert.assertEquals("{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__seedUsed\":\"123\",\"__blocked_on\":\"chan\",\"__blocked\":true,\"__seq\":1}", setup.logger.node.toString());
    }

    @Test
    public void futures_blocked_then_unblocked() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> chan; #wait { t = chan.fetch(@no_one).await().v; }");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        Assert.assertEquals("{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__seedUsed\":\"123\",\"__blocked_on\":\"chan\",\"__blocked\":true,\"__seq\":1}", setup.logger.node.toString());
        setup.drive(setup.transactor.connect(NtClient.NO_ONE));
        setup.drive(setup.transactor.send(NtClient.NO_ONE, "chan", Utility.parseJsonObject("{\"v\":74}")));
        Assert.assertEquals("{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-5106534569952410475\",\"__seedUsed\":\"123\",\"__blocked_on\":\"chan\",\"__blocked\":false,\"__seq\":5,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__message_id\":1,\"__auto_future_id\":1,\"t\":74}", setup.logger.node.toString());
    }

    @Test
    public void futures_blocked_still_blocked_wrong_user() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("@connected(who) { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> chan; #wait { t = chan.fetch(@no_one).await().v; }");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        Assert.assertEquals("{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__seedUsed\":\"123\",\"__blocked_on\":\"chan\",\"__blocked\":true,\"__seq\":1}", setup.logger.node.toString());
        setup.drive(setup.transactor.connect(A));
        setup.drive(setup.transactor.send(A, "chan", Utility.parseJsonObject("{\"v\":74}")));
        Assert.assertEquals("{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__seedUsed\":\"123\",\"__blocked_on\":\"chan\",\"__blocked\":true,\"__seq\":5,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"A\",\"authority\":\"TEST\"}},\"__messages\":{\"0\":{\"who\":{\"agent\":\"A\",\"authority\":\"TEST\"},\"channel\":\"chan\",\"message\":{\"v\":74}}},\"__message_id\":1}", setup.logger.node.toString());
    }

    @Test
    public void futures_out_of_order_rehydrate() throws Exception {
        ObjectNode node;
        {
            RealDocumentSetup setup = new RealDocumentSetup("@connected(who) { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> cha; channel<Set> chb; #wait { t = cha.fetch(@no_one).await().v; t += chb.fetch(@no_one).await().v; }");
            setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
            Assert.assertEquals("{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__seedUsed\":\"123\",\"__blocked_on\":\"cha\",\"__blocked\":true,\"__seq\":1}", setup.logger.node.toString());
            setup.drive(setup.transactor.connect(NtClient.NO_ONE));
            setup.drive(setup.transactor.send(NtClient.NO_ONE, "chb", Utility.parseJsonObject("{\"v\":50}")));
            Assert.assertEquals("{\"__state\":\"wait\",\"__constructed\":true,\"__entropy\":\"123\",\"__seedUsed\":\"123\",\"__blocked_on\":\"cha\",\"__blocked\":true,\"__seq\":5,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__messages\":{\"0\":{\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"channel\":\"chb\",\"message\":{\"v\":50}}},\"__message_id\":1}", setup.logger.node.toString());
            node = setup.logger.node.deepCopy();
        }

        RealDocumentSetup setup = new RealDocumentSetup("@connected(who) { return true; } @construct { transition #wait; } int t = 0; message Set { int v; } channel<Set> cha; channel<Set> chb; #wait { t = cha.fetch(@no_one).await().v; t += chb.fetch(@no_one).await().v; }", node);
        setup.transactor.seed(node);
        setup.drive(setup.transactor.send(NtClient.NO_ONE, "cha", Utility.parseJsonObject("{\"v\":25}")));
        Assert.assertEquals("{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-5106534569952410475\",\"__seedUsed\":\"123\",\"__blocked_on\":\"cha\",\"__blocked\":false,\"__seq\":7,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__message_id\":2,\"__auto_future_id\":2,\"t\":75}", setup.logger.node.toString());
    }

    @Test
    public void message_abort() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("@connected(who) { return who == @no_one; } message M {} channel foo(M x) { abort; }");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        setup.drive(setup.transactor.connect(NtClient.NO_ONE));
        Assert.assertEquals("{\"__constructed\":true,\"__entropy\":\"786253046697430328\",\"__seedUsed\":\"-5106534569952410475\",\"__seq\":3,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}", setup.logger.node.toString());
        setup.drive(setup.transactor.send(NtClient.NO_ONE, "foo", Utility.parseJsonObject("{}")));
        Assert.assertEquals("{\"__constructed\":true,\"__entropy\":\"8270396388693936851\",\"__seedUsed\":\"786253046697430328\",\"__seq\":6,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__message_id\":1}", setup.logger.node.toString());
    }

    @Test
    public void views() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("public int x; @construct { x = 123; } @connected (who) { x++; return true; }");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        setup.drive(setup.transactor.connect(NtClient.NO_ONE));
        Assert.assertEquals("{\"data\":{\"x\":124},\"outstanding\":[],\"blockers\":[]}", setup.transactor.getView(NtClient.NO_ONE).toString());
        setup.drive(setup.transactor.connect(A));
        Assert.assertEquals("{\"data\":{\"x\":125},\"outstanding\":[],\"blockers\":[]}", setup.transactor.getView(A).toString());
        setup.drive(setup.transactor.connect(B));
        Assert.assertEquals("{\"data\":{\"x\":126},\"outstanding\":[],\"blockers\":[]}", setup.transactor.getView(B).toString());
    }

    @Test
    public void views_no_monitor() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("public int x; @construct { x = 123; } @connected (who) { x++; return true; }", null, false);
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        setup.drive(setup.transactor.connect(NtClient.NO_ONE));
        Assert.assertEquals("{\"data\":{\"x\":124},\"outstanding\":[],\"blockers\":[]}", setup.transactor.getView(NtClient.NO_ONE).toString());
        setup.drive(setup.transactor.connect(A));
        Assert.assertEquals("{\"data\":{\"x\":125},\"outstanding\":[],\"blockers\":[]}", setup.transactor.getView(A).toString());
        setup.drive(setup.transactor.connect(B));
        Assert.assertEquals("{\"data\":{\"x\":126},\"outstanding\":[],\"blockers\":[]}", setup.transactor.getView(B).toString());
    }

    @Test
    public void command_unknown() throws Exception {
        ObjectNode state;
        {
            RealDocumentSetup setup = new RealDocumentSetup("@construct {} @connected(who) { return true; }");
            setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
            state = setup.logger.node;
        }
        RealDocumentSetup setup = new RealDocumentSetup("@construct {}");
        LivingDocument document = setup.factory.create(state, new StdOutDocumentMonitor());
        ObjectNode request = setup.transactor.forge("nope", A);
        try {
            document.__transact(request);
        } catch (DocumentRequestRejectedException drre) {
            Assert.assertEquals(DocumentRequestRejectedReason.CommandNotRecognized, drre.reason);
        }
    }

    @Test
    public void invoke_random() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("double d1; double d2; int i1; int i2; long l; int z; @construct { d1 = Random.genDouble(); d2 = Random.getDoubleGaussian() * 6; i1 = Random.genInt(); i2 = Random.genBoundInt(50); l = Random.genLong(); z = Random.genBoundInt(-1); }");
        setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
        Assert.assertEquals("{\"__constructed\":true,\"d1\":0.7231742029971469,\"d2\":2.6429286547789945,\"i1\":-535098017,\"i2\":26,\"l\":\"-5237980416576129062\",\"__entropy\":\"-5106534569952410475\",\"__seedUsed\":\"123\",\"__seq\":1}", setup.logger.node.toString());
    }

    @Test
    public void run_tests() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("test FOO {} test GOO { assert true; } test ZOO { @step; assert false; }");
        TestReportBuilder report = new TestReportBuilder();
        setup.factory.populateTestReport(report, new StdOutDocumentMonitor());
        Assert.assertEquals("TEST[FOO] HAS NO ASSERTS\n" +
                "TEST[GOO] = 100.0%\n" +
                "TEST[ZOO] = 0.0% (HAS FAILURES)\n", report.toString());
    }

    @Test
    public void blocking_test() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("@construct { transition #foo; } #foo { block; } test ZOO { @step; assert @blocked; }");
        TestReportBuilder report = new TestReportBuilder();
        setup.factory.populateTestReport(report, new StdOutDocumentMonitor());
        Assert.assertEquals("TEST[ZOO] = 100.0%\n" +
                "...DUMP:{\"__blocked\":true}\n", report.toString());
    }

    @Test
    public void pump_test() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("message M { int z; } int x = 0; channel foo(M m) { x = m.z; } test ZOO { @pump {z:123} into foo; @step; assert x == 123; }");
        TestReportBuilder report = new TestReportBuilder();
        setup.factory.populateTestReport(report, new StdOutDocumentMonitor());
        Assert.assertEquals("TEST[ZOO] = 100.0%\n", report.toString());
    }

    @Test
    public void test_invoke() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("int x = 0; #whoop { x = 123; } test ZOO { invoke #whoop; assert x == 123; }");
        TestReportBuilder report = new TestReportBuilder();
        setup.factory.populateTestReport(report, new StdOutDocumentMonitor());
        Assert.assertEquals("TEST[ZOO] = 100.0%\n" +
                "...DUMP:{\"x\":123}\n", report.toString());
    }

    @Test
    public void test_invoke_no_monitor() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("int x = 0; #whoop { x = 123; } test ZOO { invoke #whoop; assert x == 123; }");
        TestReportBuilder report = new TestReportBuilder();
        setup.factory.populateTestReport(report, null);
        Assert.assertEquals("TEST[ZOO] = 100.0%\n" +
                "...DUMP:{\"x\":123}\n", report.toString());
    }

    @Test
    public void pump_abort_rollback() throws Exception {
        RealDocumentSetup setup = new RealDocumentSetup("message M { int z; } int x = 0; channel foo(M m) { x = m.z; abort; } test ZOO { @pump {z:123} into foo; @step; assert x == 0; }");
        TestReportBuilder report = new TestReportBuilder();
        setup.factory.populateTestReport(report, new StdOutDocumentMonitor());
        Assert.assertEquals("TEST[ZOO] = 100.0%\n", report.toString());
    }

    @Test
    public void infinite_loop_1() throws Exception {
        boolean gotIt = false;
        try {
            RealDocumentSetup setup = new RealDocumentSetup("@construct { while(true) {} }");
            setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
            Assert.fail();
        } catch (GoodwillExhaustedException yay) {
            yay.printStackTrace();
            gotIt = true;
        }
        Assert.assertTrue(gotIt);
    }

    @Test
    public void infinite_loop_2() throws Exception {
        boolean gotIt = false;
        RealDocumentSetup setup = new RealDocumentSetup("@construct { transition #loop; } #loop { while(true) {} }");
        try {
            setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
            Assert.fail();
        } catch (GoodwillExhaustedException yay) {
            yay.printStackTrace();
            gotIt = true;
        }
        Assert.assertTrue(gotIt);
        setup.transactor.bill();
        Assert.assertEquals("{\"__state\":\"loop\",\"__constructed\":true,\"__entropy\":\"123\",\"__goodwill_used\":100000,\"__cost\":0,\"__billing_seq\":0}", setup.logger.node.toString());
    }

    @Test
    public void infinite_loop_bubble() throws Exception {
        boolean gotIt = false;
        try {
            RealDocumentSetup setup = new RealDocumentSetup("@connected(who) { return true; } function inf() -> int { int z = 0; while (z < 1000000) { z++; } return z; } bubble<who> x = inf();");
            setup.drive(setup.transactor.construct(NtClient.NO_ONE, Utility.createObjectNode(), "123"));
            setup.drive(setup.transactor.connect(A));
            Assert.fail();
        } catch (GoodwillExhaustedException yay) {
            yay.printStackTrace();
            gotIt = true;
        }
        Assert.assertTrue(gotIt);
    }
}