/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockArchiveDataSource implements ArchivingDataService {
  private final DataService data;
  private final HashMap<String, String> archive;
  private final HashMap<String, Integer> archiveSeq;
  private final ArrayList<String> log;
  private final ArrayList<CountDownLatch> latches;
  private final ArrayList<Runnable> backups;
  private final ArrayList<Runnable> restores;

  public MockArchiveDataSource(DataService data) {
    this.data = data;
    this.log = new ArrayList<>();
    this.latches = new ArrayList<>();
    this.archive = new HashMap<>();
    this.backups = new ArrayList<>();
    this.restores = new ArrayList<>();
    this.archiveSeq = new HashMap<>();
  }

  private synchronized void println(String x) {
    System.out.println(x);
    log.add(x);
    Iterator<CountDownLatch> it = latches.iterator();
    while (it.hasNext()) {
      CountDownLatch latch = it.next();
      latch.countDown();
      if (latch.getCount() == 0) {
        it.remove();
      }
    }
  }

  public void forceArchive(String archiveKey, String payload, int seq) {
    this.archive.put(archiveKey, payload);
    this.archiveSeq.put(archiveKey, seq);
  }

  @Override
  public void cleanUp(Key key, String archiveKey) {
    archive.remove(archiveKey);
    archiveSeq.remove(archiveKey);
    println("CLEAN:" + key.space + "/" + key.key);
  }

  public synchronized void assertLogAt(int k, String expected) {
    Assert.assertEquals(expected, log.get(k));
  }

  public synchronized void assertLogAtStartsWith(int k, String prefix) {
    Assert.assertTrue(log.get(k).startsWith(prefix));
  }

  public synchronized String getLogAt(int k) {
    return log.get(k);
  }

  private boolean failedRetryKey = false;

  public synchronized Runnable latchLogAt(int count) {
    CountDownLatch latch = new CountDownLatch(count);
    latches.add(latch);
    return () -> {
      try {
        Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
      } catch (InterruptedException ie) {
        Assert.fail();
      }
    };
  }

  public void driveBackup() {
    final Runnable backup;
    synchronized (this) {
      Assert.assertEquals(1, backups.size());
      backup = backups.remove(0);
    }
    backup.run();
  }

  public void driveRestore() {
    final Runnable restore;
    synchronized (this) {
      Assert.assertEquals(1, restores.size());
      restore = restores.remove(0);
    }
    restore.run();
  }

  @Override
  public synchronized void restore(Key key, String archiveKey, Callback<Void> callback) {
    restores.add(() -> {
      println("RESTORE-EXEC:" + key.space + "/" + key.key);
      if (key.key.contains("fail-restore")) {
        callback.failure(new ErrorCodeException(-2000));
        return;
      }
      String value;
      synchronized (archive) {
        value = archive.get(archiveKey);
      }
      if (value == null) {
        callback.failure(new ErrorCodeException(-3000));
        return;
      }
      int seq = archiveSeq.get(archiveKey);
      // TODO: sort out a better way to restore an arbitrary data source for testing? This may be good enough with the seq hack
      data.initialize(key, new RemoteDocumentUpdate(seq, seq, NtClient.NO_ONE, "restore", value, "{}", false, 1, 0, UpdateType.Internal), callback);
    });
    println("RESTORE-INIT:" + key.space + "/" + key.key);
  }

  @Override
  public synchronized void backup(Key key, Callback<BackupResult> callback) {
    if (key.key.equals("retry-key")) {
      if (!failedRetryKey) {
        failedRetryKey = true;
        callback.failure(new ErrorCodeException(-6969));
        return;
      }
    }
    backups.add(() -> {
      println("BACKUP-EXEC:" + key.space + "/" + key.key);
      String archiveKey = key.key + "_" + System.currentTimeMillis();
      if (key.key.contains("fail-backup")) {
        callback.failure(new ErrorCodeException(-1000));
        return;
      }
      data.get(key, new Callback<LocalDocumentChange>() {
        @Override
        public void success(LocalDocumentChange value) {
          synchronized (archiveKey) {
            archive.put(archiveKey, value.patch);
            archiveSeq.put(archiveKey, value.seq);
          }
          callback.success(new BackupResult(archiveKey, 0, 1L, 2L));
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    });
    println("BACKUP:" + key.space + "/" + key.key);
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    data.get(key, callback);
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    data.initialize(key, patch, callback);
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    data.patch(key, patches, callback);
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    data.compute(key, method, seq, callback);
  }

  @Override
  public void delete(Key key, Callback<Void> callback) {
    data.delete(key, callback);
  }

  @Override
  public void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback) {
    data.snapshot(key, snapshot, callback);
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    data.close(key, callback);
  }
}
