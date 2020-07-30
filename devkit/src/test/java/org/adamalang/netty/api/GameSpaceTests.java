/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.api;

import java.io.File;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.translator.env.CompilerOptions;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameSpaceTests {
  public static final TimeSource ZERO_TIME = () -> 0;

  @BeforeClass
  public static void setUp() {
    wipeTestData();
  }

  @AfterClass
  public static void tearDown() {
    wipeTestData();
  }

  private static void wipe(final File dir) {
    for (final File file : dir.listFiles()) {
      if (file.isDirectory()) {
        wipe(file);
      }
      file.delete();
    }
  }

  public static void wipeTestData() {
    final var testDir = new File("./test_data");
    testDir.mkdir();
    wipe(testDir);
  }

  @Test
  public void build_fails() throws Exception {
    var built = false;
    try {
      GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Parser_DoWhileEOS_failure.a", "Demo");
      built = true;
      Assert.fail();
    } catch (final Exception ee) {}
    Assert.assertFalse(built);
  }

  @Test
  public void build_happy() throws Exception {
    final var factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
    final var gs = new GameSpace(factory, ZERO_TIME, new File("./test_data"));
    gs.close();
  }

  @Test
  public void create_works() throws Exception {
    setup_da_bomb("foo");
  }

  @Test
  public void failure_modes() throws Exception {
    final var factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
    final var gs = new GameSpace(factory, ZERO_TIME, new File("./test_data"));
    var passes = 0;
    var sm = gs.get("foo3");
    Assert.assertNull(sm);
    sm = gs.create("foo3", NtClient.NO_ONE, Utility.createObjectNode(), "123");
    Assert.assertTrue(sm == gs.get("foo3"));
    try {
      sm = gs.create("foo3", NtClient.NO_ONE, Utility.createObjectNode(), "123");
      passes++;
      Assert.fail();
    } catch (final ErrorCodeException e) {
      Assert.assertEquals(4008, e.code);
    }
    Assert.assertEquals(0, passes);
  }

  @Test
  public void reload_after_create() throws Exception {
    setup_da_bomb("foo2");
    final var factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
    final var gs = new GameSpace(factory, ZERO_TIME, new File("./test_data"));
    gs.get("foo2");
  }

  private void setup_da_bomb(final String id) throws Exception {
    final var factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
    final var gs = new GameSpace(factory, ZERO_TIME, new File("./test_data"));
    gs.create(id, NtClient.NO_ONE, Utility.createObjectNode(), "123");
    /* MockApiResponder responder = new MockApiResponder(); sm.get(NtClient.NO_ONE,
     * responder); gs.close(); responder.assertDone(); responder.assertSize(1);
     * Assert.
     * assertEquals("{\"data\":{\"x\":\"Initializing Device\"},\"outstanding\":[],\"blockers\":[]}"
     * , responder.get(0).toString()); */
    final var data = new File("./test_data/" + id + ".jsonlog");
    Assert.assertTrue(data.exists());
    gs.close();
  }
}