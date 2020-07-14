/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.api;

import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.exceptions.ApiErrorReason;
import org.adamalang.runtime.mocks.MockApiResponder;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.env.CompilerOptions;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

public class CoreHandlerTests {
    @BeforeClass
    public static void setUp() {
        GameSpaceTests.wipeTestData();
    }

    @AfterClass
    public static void tearDown() {
        GameSpaceTests.wipeTestData();
    }

    @Test
    public void noId() throws Exception {
        CoreHandler handler = new CoreHandler();
        AdamaSession session = new AdamaSession(NtClient.NO_ONE);
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, GameSpaceTests.ZERO_TIME, new File("./test_data"));
        HashMap<String, QueryVariant> query = new HashMap<>();
        MockApiResponder responder = new MockApiResponder();
        handler.handle(session, ApiMethod.Get, gs, null, query, Utility.createObjectNode(), responder);
        responder.assertErrorReason(ApiErrorReason.NoGameIdSpecified);
        gs.close();
    }

    @Test
    public void idNotFound() throws Exception {
        CoreHandler handler = new CoreHandler();
        AdamaSession session = new AdamaSession(NtClient.NO_ONE);
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, GameSpaceTests.ZERO_TIME, new File("./test_data"));
        HashMap<String, QueryVariant> query = new HashMap<>();
        MockApiResponder responder = new MockApiResponder();
        handler.handle(session, ApiMethod.Get, gs, "42", query, Utility.createObjectNode(), responder);
        responder.assertErrorReason(ApiErrorReason.GameNotFound);
        gs.close();
    }

    @Test
    public void idFoundAndGetResults() throws Exception {
        CoreHandler handler = new CoreHandler();
        AdamaSession session = new AdamaSession(NtClient.NO_ONE);
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, GameSpaceTests.ZERO_TIME, new File("./test_data"));
        gs.create("game1", NtClient.NO_ONE, Utility.createObjectNode(), "123");
        HashMap<String, QueryVariant> query = new HashMap<>();
        MockApiResponder responder = new MockApiResponder();
        handler.handle(session, ApiMethod.Get, gs, "game1", query, Utility.createObjectNode(), responder);
        gs.close();
        responder.assertDone();
        responder.assertSize(1);
        Assert.assertEquals("{\"data\":{\"x\":\"Tick\"},\"outstanding\":[],\"blockers\":[]}", responder.get(0).toString());
    }

    @Test
    public void idFoundAndSubscribeResults() throws Exception {
        CoreHandler handler = new CoreHandler();
        AdamaSession session = new AdamaSession(NtClient.NO_ONE);
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, GameSpaceTests.ZERO_TIME, new File("./test_data"));
        gs.create("game2", NtClient.NO_ONE, Utility.createObjectNode(), "123");
        HashMap<String, QueryVariant> query = new HashMap<>();
        query.put("subscribe", new QueryVariant("true"));
        MockApiResponder responder = new MockApiResponder();
        handler.handle(session, ApiMethod.Get, gs, "game2", query, Utility.createObjectNode(), responder);
        gs.close();
        responder.assertNotDone();
        responder.assertSize(1);
        Assert.assertEquals("{\"data\":{\"x\":\"Tick\"},\"outstanding\":[],\"blockers\":[]}", responder.get(0).toString());
    }

    @Test
    public void create() throws Exception {
        CoreHandler handler = new CoreHandler();
        AdamaSession session = new AdamaSession(NtClient.NO_ONE);
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, GameSpaceTests.ZERO_TIME, new File("./test_data"));
        HashMap<String, QueryVariant> query = new HashMap<>();
        MockApiResponder responder = new MockApiResponder();
        handler.handle(session, ApiMethod.Post, gs, null, query, Utility.createObjectNode(), responder);
        responder.assertDone();
        responder.assertSize(1);
        Assert.assertTrue(responder.get(0).has("id"));
    }

    @Test
    public void createWithEntropy() throws Exception {
        CoreHandler handler = new CoreHandler();
        AdamaSession session = new AdamaSession(NtClient.NO_ONE);
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, GameSpaceTests.ZERO_TIME, new File("./test_data"));
        HashMap<String, QueryVariant> query = new HashMap<>();
        query.put("entropy", new QueryVariant("123"));
        MockApiResponder responder = new MockApiResponder();
        handler.handle(session, ApiMethod.Post, gs, null, query, Utility.createObjectNode(), responder);
        responder.assertDone();
        responder.assertSize(1);
        Assert.assertTrue(responder.get(0).has("id"));
    }

    @Test
    public void sendNoChannel() throws Exception {
        CoreHandler handler = new CoreHandler();
        AdamaSession session = new AdamaSession(NtClient.NO_ONE);
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, GameSpaceTests.ZERO_TIME, new File("./test_data"));
        gs.create("game3", NtClient.NO_ONE, Utility.createObjectNode(), "123");
        HashMap<String, QueryVariant> query = new HashMap<>();
        MockApiResponder responder = new MockApiResponder();
        handler.handle(session, ApiMethod.Post, gs, "game3", query, Utility.createObjectNode(), responder);
        gs.close();
        responder.assertErrorReason(ApiErrorReason.NoChannelSpecified);
    }

    @Test
    public void sendNoGame() throws Exception {
        CoreHandler handler = new CoreHandler();
        AdamaSession session = new AdamaSession(NtClient.NO_ONE);
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, GameSpaceTests.ZERO_TIME, new File("./test_data"));
        HashMap<String, QueryVariant> query = new HashMap<>();
        query.put("channel", new QueryVariant("foo"));
        MockApiResponder responder = new MockApiResponder();
        handler.handle(session, ApiMethod.Post, gs, "game4", query, Utility.createObjectNode(), responder);
        gs.close();
        responder.assertErrorReason(ApiErrorReason.GameNotFound);
    }

    @Test
    public void sendGoodWhileConnected() throws Exception {
        CoreHandler handler = new CoreHandler();
        AdamaSession session = new AdamaSession(NtClient.NO_ONE);
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, GameSpaceTests.ZERO_TIME, new File("./test_data"));
        LivingDocumentStateMachine sm = gs.create("game5", NtClient.NO_ONE, Utility.createObjectNode(), "123");
        sm.getAndSubscribe(session.who, new MockApiResponder());
        HashMap<String, QueryVariant> query = new HashMap<>();
        query.put("channel", new QueryVariant("foo"));
        MockApiResponder responder = new MockApiResponder();
        handler.handle(session, ApiMethod.Post, gs, "game5", query, Utility.createObjectNode(), responder);
        gs.close();
        responder.assertDone();
        responder.assertSize(1);
        Assert.assertEquals("{\"seq\":4}", responder.get(0).toString());
    }

    @Test
    public void sendFailsWhileNotConnected() throws Exception {
        CoreHandler handler = new CoreHandler();
        AdamaSession session = new AdamaSession(NtClient.NO_ONE);
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, GameSpaceTests.ZERO_TIME, new File("./test_data"));
        gs.create("game6", NtClient.NO_ONE, Utility.createObjectNode(), "123");
        HashMap<String, QueryVariant> query = new HashMap<>();
        query.put("channel", new QueryVariant("foo"));
        MockApiResponder responder = new MockApiResponder();
        handler.handle(session, ApiMethod.Post, gs, "game6", query, Utility.createObjectNode(), responder);
        gs.close();
        responder.assertDone();
        responder.assertErrorReason(ApiErrorReason.SendMessageFailure);
    }

    @Test
    public void crashOnCreate() throws Exception {
        CoreHandler handler = new CoreHandler();
        AdamaSession session = new AdamaSession(NtClient.NO_ONE);
        LivingDocumentFactory factory = LivingDocumentTests.compile("@construct { while(true) {} }");
        GameSpace gs = new GameSpace(factory, GameSpaceTests.ZERO_TIME, new File("./test_data"));
        HashMap<String, QueryVariant> query = new HashMap<>();
        MockApiResponder responder = new MockApiResponder();
        handler.handle(session, ApiMethod.Post, gs, null, query, Utility.createObjectNode(), responder);
        responder.assertErrorReason(ApiErrorReason.InternalIssue);
    }

}
