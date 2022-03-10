/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.disk.files.SnapshotFileStreamEvents;
import org.adamalang.disk.files.SnapshotHeader;
import org.adamalang.disk.wal.WriteAheadMessage;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.LocalDocumentChange;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DocumentMemoryLogTests {

  private static final RemoteDocumentUpdate UPDATE_1 = new RemoteDocumentUpdate(1, 1, NtClient.NO_ONE, "REQUEST", "{\"x\":1,\"y\":4}", "{\"x\":0,\"y\":0}", false, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_2 = new RemoteDocumentUpdate(2, 2, null, "REQUEST", "{\"x\":2}", "{\"x\":1,\"z\":3}", true, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_3 = new RemoteDocumentUpdate(3, 3, null, "REQUEST", "{\"x\":3}", "{\"x\":2,\"z\":2}", true, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_4 = new RemoteDocumentUpdate(4, 4, null, "REQUEST", "{\"x\":4}", "{\"x\":3,\"z\":1}", true, 0, 100, UpdateType.AddUserData);

  @Test
  public void happy() throws Exception {
    DocumentMemoryLog log = makeLog();
    try {
      log.get_Load();
      Assert.fail();
    } catch (IOException ioe) {
      Assert.assertTrue(ioe instanceof FileNotFoundException);
    }
    init(log);
    assertGet(log, "{\"x\":1,\"y\":4}", 1);
    patch(log, UPDATE_2);
    patch(log, UPDATE_3, UPDATE_4);
    assertGet(log, "{\"x\":4,\"y\":4}", 4);
    snapshot(log, "{\"x\":10,\"y\":10}", 4, 5);
    assertGet(log, "{\"x\":10,\"y\":10}", 4);
    log.flush();
    assertFile(log, "{\"x\":10,\"y\":10}", 3);
  }

  private DocumentMemoryLog makeLog() throws Exception {
    File path = new File(File.createTempFile("prefix", "suffix").getParentFile(), "_log_" + System.currentTimeMillis());
    path.mkdir();
    return new DocumentMemoryLog(new Key("space", "key"), path);
  }

  private void init(DocumentMemoryLog log) {
    WriteAheadMessage.Initialize initialize = new WriteAheadMessage.Initialize();
    initialize.initialize = new WriteAheadMessage.Change();
    initialize.initialize.redo = UPDATE_1.redo;
    initialize.initialize.seq_end = UPDATE_1.seqEnd;
    log.apply(initialize);
  }

  private void assertGet(DocumentMemoryLog log, String state, int reads) throws Exception {
    LocalDocumentChange change = log.get_Load();
    Assert.assertEquals(state, change.patch);
    Assert.assertEquals(reads, change.reads);
  }

  private void patch(DocumentMemoryLog log, RemoteDocumentUpdate... updates) {
    WriteAheadMessage.Patch patch = new WriteAheadMessage.Patch();
    patch.changes = new WriteAheadMessage.Change[updates.length];
    for (int k = 0; k < updates.length; k++) {
      patch.changes[k] = new WriteAheadMessage.Change();
      patch.changes[k].copyFrom(updates[k]);
    }
    log.apply(patch);
  }

  private void snapshot(DocumentMemoryLog log, String snap, int seq, int history) {
    WriteAheadMessage.Snapshot snapshot = new WriteAheadMessage.Snapshot();
    snapshot.document = snap;
    snapshot.seq = seq;
    snapshot.history = history;
    log.apply(snapshot);
  }

  private void assertFile(DocumentMemoryLog log, String finalDoc, int undoLimit) throws Exception {
    File snapshot = log.suffixFile("SNAPSHOT");
    FileInputStream input = new FileInputStream(snapshot);
    try {
      AtomicInteger asserts = new AtomicInteger(0);
      SnapshotFileStreamEvents.read(new DataInputStream(input), new SnapshotFileStreamEvents() {
        @Override
        public boolean onHeader(SnapshotHeader header) throws IOException {
          System.err.println("HEADER:" + header.toString());
          return true;
        }

        @Override
        public boolean onDocument(byte[] document) throws IOException {
          Assert.assertEquals(finalDoc, new String(document));
          asserts.incrementAndGet();
          return true;
        }

        @Override
        public boolean onUndo(int seq, String undo) throws IOException {
          System.err.println("SEQ:" + seq + "->" + undo);
          switch (seq) {
            case 4:
              Assert.assertEquals("{\"x\":3,\"z\":1}", undo);
              asserts.incrementAndGet();
              break;
            case 3:
              Assert.assertEquals("{\"x\":2,\"z\":2}", undo);
              asserts.incrementAndGet();
              break;
            case 2:
              Assert.assertEquals("{\"x\":1,\"z\":3}", undo);
              asserts.incrementAndGet();
              break;
            default:
              Assert.fail();
          }
          return true;
        }

        @Override
        public void onFinished(boolean complete) throws IOException {
          Assert.assertTrue(complete);
          asserts.incrementAndGet();
        }
      });
      Assert.assertEquals(2 + undoLimit, asserts.get());
    } finally {
      input.close();
    }
  }

  @Test
  public void flush_early() throws Exception {
    DocumentMemoryLog log = makeLog();
    try {
      log.get_Load();
      Assert.fail();
    } catch (IOException ioe) {
      Assert.assertTrue(ioe instanceof FileNotFoundException);
    }
    init(log);
    assertGet(log, "{\"x\":1,\"y\":4}", 1);
    patch(log, UPDATE_2);
    log.flush();
    patch(log, UPDATE_3, UPDATE_4);
    assertGet(log, "{\"x\":4,\"y\":4}", 4);
    snapshot(log, "{\"x\":10,\"y\":10}", 4, 5);
    assertGet(log, "{\"x\":10,\"y\":10}", 4);
    log.flush();
    assertFile(log, "{\"x\":10,\"y\":10}", 3);
  }

  @Test
  public void flush_often() throws Exception {
    DocumentMemoryLog log = makeLog();
    try {
      log.get_Load();
      Assert.fail();
    } catch (IOException ioe) {
      Assert.assertTrue(ioe instanceof FileNotFoundException);
    }
    init(log);
    assertGet(log, "{\"x\":1,\"y\":4}", 1);
    patch(log, UPDATE_2);
    log.flush();
    patch(log, UPDATE_3, UPDATE_4);
    log.flush();
    assertGet(log, "{\"x\":4,\"y\":4}", 4);
    log.flush();
    snapshot(log, "{\"x\":10,\"y\":10}", 4, 5);
    log.flush();
    assertGet(log, "{\"x\":10,\"y\":10}", 4);
    log.flush();
    assertFile(log, "{\"x\":10,\"y\":10}", 3);
  }

  @Test
  public void limit_history() throws Exception {
    DocumentMemoryLog log = makeLog();
    try {
      log.get_Load();
      Assert.fail();
    } catch (IOException ioe) {
      Assert.assertTrue(ioe instanceof FileNotFoundException);
    }
    init(log);
    assertGet(log, "{\"x\":1,\"y\":4}", 1);
    patch(log, UPDATE_2);
    patch(log, UPDATE_3, UPDATE_4);
    assertGet(log, "{\"x\":4,\"y\":4}", 4);
    snapshot(log, "{\"x\":10,\"y\":10}", 4, 1);
    assertGet(log, "{\"x\":10,\"y\":10}", 4);
    log.flush();
    assertGet(log, "{\"x\":10,\"y\":10}", 2);
    assertFile(log, "{\"x\":10,\"y\":10}", 1);
  }

  @Test
  public void flush_early_limit_history() throws Exception {
    DocumentMemoryLog log = makeLog();
    try {
      log.get_Load();
      Assert.fail();
    } catch (IOException ioe) {
      Assert.assertTrue(ioe instanceof FileNotFoundException);
    }
    init(log);
    assertGet(log, "{\"x\":1,\"y\":4}", 1);
    patch(log, UPDATE_2);
    log.flush();
    patch(log, UPDATE_3, UPDATE_4);
    assertGet(log, "{\"x\":4,\"y\":4}", 4);
    snapshot(log, "{\"x\":10,\"y\":10}", 4, 2);
    assertGet(log, "{\"x\":10,\"y\":10}", 4);
    log.flush();
    assertFile(log, "{\"x\":10,\"y\":10}", 2);
  }

  @Test
  public void flush_early_often_limit_history() throws Exception {
    DocumentMemoryLog log = makeLog();
    try {
      log.get_Load();
      Assert.fail();
    } catch (IOException ioe) {
      Assert.assertTrue(ioe instanceof FileNotFoundException);
    }
    init(log);
    assertGet(log, "{\"x\":1,\"y\":4}", 1);
    patch(log, UPDATE_2);
    log.flush();
    patch(log, UPDATE_3);
    log.flush();
    patch(log, UPDATE_4);
    log.flush();
    assertGet(log, "{\"x\":4,\"y\":4}", 4);
    snapshot(log, "{\"x\":10,\"y\":10}", 4, 2);
    log.flush();
    assertGet(log, "{\"x\":10,\"y\":10}", 3);
    log.flush();
    assertFile(log, "{\"x\":10,\"y\":10}", 2);
  }

  @Test
  public void no_reads() throws Exception {
    DocumentMemoryLog log = makeLog();
    try {
      log.get_Load();
      Assert.fail();
    } catch (IOException ioe) {
      Assert.assertTrue(ioe instanceof FileNotFoundException);
    }
    init(log);
    assertGet(log, "{\"x\":1,\"y\":4}", 1);
    patch(log, UPDATE_2);
    patch(log, UPDATE_3);
    patch(log, UPDATE_4);
    snapshot(log, "{\"x\":10,\"y\":10}", 4, 2);
    log.flush();
    assertFile(log, "{\"x\":10,\"y\":10}", 2);
  }

  @Test
  public void no_reads_flush_often() throws Exception {
    DocumentMemoryLog log = makeLog();
    try {
      log.get_Load();
      Assert.fail();
    } catch (IOException ioe) {
      Assert.assertTrue(ioe instanceof FileNotFoundException);
    }
    init(log);
    assertGet(log, "{\"x\":1,\"y\":4}", 1);
    patch(log, UPDATE_2);
    log.flush();
    patch(log, UPDATE_3);
    log.flush();
    patch(log, UPDATE_4);
    log.flush();
    snapshot(log, "{\"x\":10,\"y\":10}", 4, 2);
    log.flush();
    assertFile(log, "{\"x\":10,\"y\":10}", 2);
    {
      Assert.assertFalse(cleanStateOf(log).canInitialize());
    }
  }

  private DocumentMemoryLog cleanStateOf(DocumentMemoryLog log) {
    return new DocumentMemoryLog(new Key("space", "key"), log.spacePath);
  }

  @Test
  public void no_reads_batch_big_flush_once() throws Exception {
    DocumentMemoryLog log = makeLog();
    try {
      log.get_Load();
      Assert.fail();
    } catch (IOException ioe) {
      Assert.assertTrue(ioe instanceof FileNotFoundException);
    }
    Assert.assertTrue(log.canInitialize());
    init(log);
    assertGet(log, "{\"x\":1,\"y\":4}", 1);
    Assert.assertTrue(log.canPatch(UPDATE_2.seqBegin));
    patch(log, UPDATE_2, UPDATE_3, UPDATE_4);
    snapshot(log, "{\"x\":10,\"y\":10}", 4, 2);
    log.flush();
    assertFile(log, "{\"x\":10,\"y\":10}", 2);
    {
      Assert.assertFalse(cleanStateOf(log).canInitialize());
    }
  }

  @Test
  public void delete_remake() throws Exception {
    DocumentMemoryLog log = makeLog();
    try {
      log.get_Load();
      Assert.fail();
    } catch (IOException ioe) {
      Assert.assertTrue(ioe instanceof FileNotFoundException);
    }
    init(log);
    assertGet(log, "{\"x\":1,\"y\":4}", 1);
    patch(log, UPDATE_2, UPDATE_3, UPDATE_4);
    log.delete();
    Assert.assertFalse(log.isAvailable());
    init(log);
    assertGet(log, "{\"x\":1,\"y\":4}", 1);
    patch(log, UPDATE_2, UPDATE_3, UPDATE_4);
    log.flush();
    assertFile(log, "{\"x\":4,\"y\":4}", 3);
    {
      Assert.assertFalse(cleanStateOf(log).canInitialize());
    }
    log.delete();
    log.flush();
    Assert.assertFalse(log.isAvailable());
  }

  @Test
  public void delete_can_redo() throws Exception {
    DocumentMemoryLog log = makeLog();
    try {
      log.get_Load();
      Assert.fail();
    } catch (IOException ioe) {
      Assert.assertTrue(ioe instanceof FileNotFoundException);
    }
    Assert.assertTrue(makeLog().canInitialize());
    init(log);
    assertGet(log, "{\"x\":1,\"y\":4}", 1);
    patch(log, UPDATE_2, UPDATE_3, UPDATE_4);
    log.delete();
    Assert.assertFalse(log.isAvailable());
    Assert.assertTrue(makeLog().canInitialize());
    init(log);
    assertGet(log, "{\"x\":1,\"y\":4}", 1);
    patch(log, UPDATE_2, UPDATE_3, UPDATE_4);
    log.flush();
    assertFile(log, "{\"x\":4,\"y\":4}", 3);
    {
      Assert.assertFalse(cleanStateOf(log).canInitialize());
    }
    log.delete();
    log.flush();
    Assert.assertFalse(log.isAvailable());
  }

  @Test
  public void load() throws Exception {
    DocumentMemoryLog log = makeLog();
    DocumentMemoryLog copy = cleanStateOf(log);
    {
      init(log);
      patch(log, UPDATE_2, UPDATE_3, UPDATE_4);
      log.flush();
    }
    assertFile(copy, "{\"x\":4,\"y\":4}", 3);
  }

  @Test
  public void ensureLoaded() throws Exception {
    DocumentMemoryLog log = makeLog();
    DocumentMemoryLog copy = cleanStateOf(log);
    {
      init(log);
      patch(log, UPDATE_2, UPDATE_3, UPDATE_4);
      log.flush();
    }
    Assert.assertFalse(copy.canInitialize());
    Assert.assertTrue(log.canPatch(5));
    Assert.assertFalse(copy.canPatch(5));
    copy.ensureLoaded(new Callback<Void>() {
      @Override
      public void success(Void value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    });
    Assert.assertFalse(copy.canInitialize());
    Assert.assertTrue(copy.canPatch(5));
    copy.ensureLoaded(new Callback<Void>() {
      @Override
      public void success(Void value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    });
    assertFile(copy, "{\"x\":4,\"y\":4}", 3);
  }
}
