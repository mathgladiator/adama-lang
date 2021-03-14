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
    final var gs = new GameSpace("name", factory, ZERO_TIME, new File("./test_data"));
    gs.close();
  }

  @Test
  public void create_works() throws Exception {
    setup_da_bomb(561);
  }

  @Test
  public void failure_modes() throws Exception {
    final var factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
    final var gs = new GameSpace("name", factory, ZERO_TIME, new File("./test_data"));
    var passes = 0;
    var sm = gs.get(123L);
    Assert.assertNull(sm);
    sm = gs.create(123L, NtClient.NO_ONE, Utility.createObjectNode(), "123");
    Assert.assertTrue(sm == gs.get(123L));
    try {
      sm = gs.create(123L, NtClient.NO_ONE, Utility.createObjectNode(), "123");
      passes++;
      Assert.fail();
    } catch (final ErrorCodeException e) {
      Assert.assertEquals(4008, e.code);
    }
    Assert.assertEquals(0, passes);
  }

  @Test
  public void reload_after_create() throws Exception {
    setup_da_bomb(42L);
    final var factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
    final var gs = new GameSpace("name", factory, ZERO_TIME, new File("./test_data"));
    gs.get(42L);
  }

  private void setup_da_bomb(final long id) throws Exception {
    final var factory = GameSpace.buildLivingDocumentFactory(new File("./test_code/"), CompilerOptions.start().make(), "Demo_Bomb_success.a", "Demo");
    final var gs = new GameSpace("name", factory, ZERO_TIME, new File("./test_data"));
    gs.create(id, NtClient.NO_ONE, Utility.createObjectNode(), "123");
    String pathname = "./test_data/" + id + ".jsonlog";
    final var data = new File(pathname);
    Assert.assertTrue(data.exists());
    gs.close();
  }
}