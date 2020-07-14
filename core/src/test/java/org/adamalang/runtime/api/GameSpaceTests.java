/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.api;

import org.adamalang.runtime.contracts.TimeSource;
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

public class GameSpaceTests {

    private static void wipe(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                wipe(file);
            }
            file.delete();
        }
    }

    public static void wipeTestData() {
        File testDir = new File("./test_data");
        testDir.mkdir();
        wipe(testDir);
    }

    public static final TimeSource ZERO_TIME = new TimeSource() {
        @Override
        public long nowMilliseconds() {
            return 0;
        }
    };

    @BeforeClass
    public static void setUp() {
        wipeTestData();
    }

    @AfterClass
    public static void tearDown() {
        wipeTestData();
    }

    @Test
    public void build_happy() throws Exception {
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, ZERO_TIME, new File("./test_data"));
        gs.close();
    }

    private void setup_da_bomb(String id) throws Exception {
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, ZERO_TIME, new File("./test_data"));
        LivingDocumentStateMachine sm = gs.create(id, NtClient.NO_ONE, Utility.createObjectNode(), "123");
        MockApiResponder responder = new MockApiResponder();
        sm.get(NtClient.NO_ONE, responder);
        gs.close();
        responder.assertDone();
        responder.assertSize(1);
        Assert.assertEquals("{\"data\":{\"x\":\"Tick\"},\"outstanding\":[],\"blockers\":[]}", responder.get(0).toString());
        File data = new File("./test_data/" + id + ".jsonlog");
        Assert.assertTrue(data.exists());
    }

    @Test
    public void create_works() throws Exception {
        setup_da_bomb("foo");
    }

    @Test
    public void reload_after_create() throws Exception {
        setup_da_bomb("foo2");
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, ZERO_TIME, new File("./test_data"));
        LivingDocumentStateMachine sm = gs.get("foo2");
        MockApiResponder responder = new MockApiResponder();
        sm.get(NtClient.NO_ONE, responder);
        gs.close();
        responder.assertDone();
        responder.assertSize(1);
        Assert.assertEquals("{\"data\":{\"x\":\"Tick\"},\"outstanding\":[],\"blockers\":[]}", responder.get(0).toString());
    }

    @Test
    public void failure_modes() throws Exception {
        LivingDocumentFactory factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
        GameSpace gs = new GameSpace(factory, ZERO_TIME, new File("./test_data"));
        int passes = 0;
        LivingDocumentStateMachine sm = gs.get("foo3");
        Assert.assertNull(sm);
        sm = gs.create("foo3", NtClient.NO_ONE, Utility.createObjectNode(), "123");
        Assert.assertTrue(sm == gs.get("foo3"));
        try {
            sm = gs.create("foo3", NtClient.NO_ONE, Utility.createObjectNode(), "123");
            passes++;
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("Backing File Already Exists:foo3", e.getMessage());
        }
        Assert.assertEquals(0, passes);
    }

    @Test
    public void build_fails() throws Exception {
        boolean built = false;
        try {
            GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Parser_DoWhileEOS_failure.a", "Demo");
            built = true;
            Assert.fail();
        } catch (Exception ee) {
        }
        Assert.assertFalse(built);
    }
}