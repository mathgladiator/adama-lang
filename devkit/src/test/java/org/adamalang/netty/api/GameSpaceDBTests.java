/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.api;

import java.io.File;
import java.nio.file.Files;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.translator.env.CompilerOptions;
import org.junit.Assert;
import org.junit.Test;

public class GameSpaceDBTests {
  @Test
  public void gameFound() throws Exception {
    GameSpaceTests.wipeTestData();
    final var db = new GameSpaceDB(new File("./test_code"), new File("./test_data"), CompilerOptions.start().make(), TimeSource.REAL_TIME);
    final var gs = db.getOrCreate("Demo_Bomb_success.a");
    Assert.assertNotNull(gs);
    final var again = db.getOrCreate("Demo_Bomb_success.a");
    Assert.assertTrue(gs == again);
  }

  @Test
  public void gameFoundBadDataPath() throws Exception {
    GameSpaceTests.wipeTestData();
    final var db = new GameSpaceDB(new File("./test_code"), new File("./test_data/bad"), CompilerOptions.start().make(), TimeSource.REAL_TIME);
    new File("./test_data/bad").mkdir();
    Files.writeString(new File("./test_data/bad/Demo_Bomb_success.a").toPath(), "blah");
    try {
      db.getOrCreate("Demo_Bomb_success.a");
      Assert.fail();
    } catch (final ErrorCodeException e) {
      Assert.assertEquals(5001, e.code);
    }
  }

  @Test
  public void gameFoundCreateDir() throws Exception {
    GameSpaceTests.wipeTestData();
    final var db = new GameSpaceDB(new File("./test_code"), new File("./test_data/needs_create"), CompilerOptions.start().make(), TimeSource.REAL_TIME);
    final var gs = db.getOrCreate("Demo_Bomb_success.a");
    Assert.assertNotNull(gs);
  }

  @Test
  public void gameNotFound() throws Exception {
    GameSpaceTests.wipeTestData();
    final var db = new GameSpaceDB(new File("./test_code"), new File("./test_data"), CompilerOptions.start().make(), TimeSource.REAL_TIME);
    try {
      db.getOrCreate("xyz.a");
      Assert.fail();
    } catch (final ErrorCodeException drre) {
      Assert.assertEquals(40001, drre.code);
    }
  }

  @Test
  public void gameSourceRootMustExist() throws Exception {
    GameSpaceTests.wipeTestData();
    try {
      new GameSpaceDB(new File("./test_code/nope"), new File("./test_data/bad"), CompilerOptions.start().make(), TimeSource.REAL_TIME);
      Assert.fail();
    } catch (final Exception e) {
      Assert.assertEquals("Schema root: `bad` does not exist", e.getMessage());
    }
  }

  @Test
  public void sanity() throws Exception {
    GameSpaceTests.wipeTestData();
    new GameSpaceDB(new File("./test_code"), new File("./test_data"), CompilerOptions.start().make(), TimeSource.REAL_TIME);
  }
}
