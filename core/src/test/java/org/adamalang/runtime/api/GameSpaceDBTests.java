/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.api;

import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.DocumentRequestRejectedException;
import org.adamalang.runtime.exceptions.DocumentRequestRejectedReason;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

public class GameSpaceDBTests {
    @Test
    public void sanity() throws Exception {
        GameSpaceTests.wipeTestData();
        GameSpaceDB db = new GameSpaceDB(new File("./test_code"), new File("./test_data"), TimeSource.REAL_TIME);
    }


    @Test
    public void gameNotFound() throws Exception {
        GameSpaceTests.wipeTestData();
        GameSpaceDB db = new GameSpaceDB(new File("./test_code"), new File("./test_data"), TimeSource.REAL_TIME);
        try {
            db.getOrCreate("xyz.a");
            Assert.fail();
        } catch (DocumentRequestRejectedException drre) {
            Assert.assertEquals(DocumentRequestRejectedReason.GamespaceNotFound, drre.reason);
        }
    }

    @Test
    public void gameFound() throws Exception {
        GameSpaceTests.wipeTestData();
        GameSpaceDB db = new GameSpaceDB(new File("./test_code"), new File("./test_data"), TimeSource.REAL_TIME);
        GameSpace gs = db.getOrCreate("Demo_Bomb_success.a");
        Assert.assertNotNull(gs);
        GameSpace again = db.getOrCreate("Demo_Bomb_success.a");
        Assert.assertTrue(gs == again);
    }

    @Test
    public void gameFoundCreateDir() throws Exception {
        GameSpaceTests.wipeTestData();
        GameSpaceDB db = new GameSpaceDB(new File("./test_code"), new File("./test_data/needs_create"), TimeSource.REAL_TIME);
        GameSpace gs = db.getOrCreate("Demo_Bomb_success.a");
        Assert.assertNotNull(gs);
    }

    @Test
    public void gameFoundBadDataPath() throws Exception {
        GameSpaceTests.wipeTestData();
        GameSpaceDB db = new GameSpaceDB(new File("./test_code"), new File("./test_data/bad"), TimeSource.REAL_TIME);
        new File("./test_data/bad").mkdir();
        Files.writeString(new File("./test_data/bad/Demo_Bomb_success.a").toPath(), "blah");
        try {
            db.getOrCreate("Demo_Bomb_success.a");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("Data root: Demo_Bomb_success.a either does not exist or is a file", e.getMessage());
        }
    }

    @Test
    public void gameSourceRootMustExist() throws Exception {
        GameSpaceTests.wipeTestData();
        try {
            new GameSpaceDB(new File("./test_code/nope"), new File("./test_data/bad"), TimeSource.REAL_TIME);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("Source root: `bad` does not exist", e.getMessage());
        }
    }
}
