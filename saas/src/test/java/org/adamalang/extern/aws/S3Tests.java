/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.extern.aws;

import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.contracts.BackupService;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.AssetRequest;
import org.adamalang.web.assets.AssetStream;
import org.adamalang.web.assets.AssetUploadBody;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientBaseMetrics;
import org.adamalang.web.service.WebConfig;
import org.checkerframework.checker.units.qual.K;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class S3Tests {

  @FunctionalInterface
  private static interface S3Flow {
    void run(S3 s3) throws Exception;
  }

  private void flow(S3Flow body) throws Exception {
    File archivePath = File.createTempFile("ADAMATEST_", "System-" + System.currentTimeMillis());
    archivePath.delete();
    Assert.assertTrue(archivePath.mkdirs());
    WebClientBase base = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), new WebConfig(new ConfigObject(Json.newJsonObject())));
    try {
      File configFile = new File("./saas/aws.config.json");
      if (!configFile.exists()) {
        configFile = new File("./aws.config.json");
      }
      Assume.assumeTrue(configFile.exists());
      ConfigObject co = new ConfigObject(Json.parseJsonObject(Files.readString(configFile.toPath())));
      co.strOf("archive", archivePath.getAbsolutePath());
      System.err.println("archive-path:" + archivePath.getAbsolutePath());
      AWSConfig config = new AWSConfig(co);
      S3 s3 = new S3(base, config, new AWSMetrics(new NoOpMetricsFactory()));
      body.run(s3);
    } finally {
      for (File present : archivePath.listFiles()) {
        if (present.isDirectory()) {
          for (File child : present.listFiles()) {
            child.delete();
          }
        }
        present.delete();
      }
      archivePath.delete();
      base.shutdown();
    }
  }

  private NtAsset assetOf(File file, String contentType) throws Exception {
    FileInputStream input = new FileInputStream(file);
    try {
      MessageDigest md5 = Hashing.md5();
      MessageDigest sha384 = Hashing.sha384();
      byte[] chunk = new byte[8196];
      int sz;
      long len = 0;
      while ((sz = input.read(chunk)) >= 0) {
        md5.update(chunk, 0, sz);
        sha384.update(chunk, 0, sz);
        len += sz;
      }
      return new NtAsset(ProtectedUUID.generate(), file.getName(), contentType, len, Hashing.finishAndEncode(md5), Hashing.finishAndEncode(sha384));
    } finally {
      input.close();
    }
  }

  @Test
  public void backup() throws Exception {
    flow((s3) -> {
      {
        CountDownLatch latchPut = new CountDownLatch(1);
        s3.backup(new Key("space", "backupy"), 50, BackupService.Reason.Deployment, "{\"doc\"}", new Callback<Void>() {
          @Override
          public void success(Void value) {
            latchPut.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {}
        });
        Assert.assertTrue(latchPut.await(50000, TimeUnit.MILLISECONDS));
      }
      ArrayList<S3.BackupListing> found = new ArrayList<>();
      {
        CountDownLatch latchList = new CountDownLatch(1);
        s3.listBackups(new Key("space", "backupy"), new Callback<ArrayList<S3.BackupListing>>() {
          @Override
          public void success(ArrayList<S3.BackupListing> listing) {
            System.err.println("SIZE OF LIST:" + listing.size());
            for (S3.BackupListing item : listing) {
              found.add(item);
              System.err.println(item.date + "/" + item.seq + "/" + item.reason);
              CountDownLatch latchFetch = new CountDownLatch(1);
              s3.fetchBackup(new Key("space", "backupy"), item, new Callback<String>() {
                @Override
                public void success(String value) {
                  System.err.println("--->" + value);
                  latchFetch.countDown();
                }

                @Override
                public void failure(ErrorCodeException ex) {

                }
              });
              try {
                latchFetch.await(10000, TimeUnit.MILLISECONDS);
              } catch (InterruptedException ie) {

              }
            }

            latchList.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        Assert.assertTrue(latchList.await(50000, TimeUnit.MILLISECONDS));
      }
      {
        for (S3.BackupListing toDelete : found) {
          CountDownLatch latchDelete = new CountDownLatch(1);
          s3.deleteBackup(new Key("space", "backupy"), toDelete, new Callback<Void>() {
            @Override
            public void success(Void value) {
              latchDelete.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {

            }
          });
          Assert.assertTrue(latchDelete.await(50000, TimeUnit.MILLISECONDS));
        }
        {
          CountDownLatch latchList = new CountDownLatch(1);
          s3.listBackups(new Key("space", "backupy"), new Callback<ArrayList<S3.BackupListing>>() {
            @Override
            public void success(ArrayList<S3.BackupListing> listing) {
              System.err.println("POST_DELETE_SIZE:" + listing.size());
              for (S3.BackupListing item : listing) {
                System.err.println(item.date + "/" + item.seq + "/" + item.reason);
              }
              latchList.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {

            }
          });
          Assert.assertTrue(latchList.await(50000, TimeUnit.MILLISECONDS));
        }
      }
    });
  }

  @Test
  public void smallAssetWithListing() throws Exception {
    flow((s3) -> {
      String key = "key-" + ProtectedUUID.generate();
      File tempAsset = File.createTempFile("ADAMATEST_", "temp");
      Files.write(tempAsset.toPath(), "This is a small file".getBytes(StandardCharsets.UTF_8));
      NtAsset asset = assetOf(tempAsset, "text/plain");
      CountDownLatch latchPut = new CountDownLatch(1);
      s3.upload(new Key("space", key), asset, AssetUploadBody.WRAP(tempAsset), new Callback<Void>() {
        @Override
        public void success(Void value) {
          latchPut.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          System.err.println("Failure:" + ex.code + "::" + ex.getMessage());
        }
      });
      Assert.assertTrue(latchPut.await(30000, TimeUnit.MILLISECONDS));
      CountDownLatch latchGet = new CountDownLatch(2);
      ByteArrayOutputStream getResults = new ByteArrayOutputStream();
      s3.request(new AssetRequest("space", key, asset.id), new AssetStream() {
        @Override
        public void headers(long length, String contentType, String md5) {
          Assert.assertEquals(asset.size, length);
          Assert.assertEquals(asset.contentType, contentType);
          Assert.assertEquals(asset.md5, md5);
          latchGet.countDown();
        }

        @Override
        public void body(byte[] chunk, int offset, int length, boolean last) {
          getResults.write(chunk, offset, length);
          if (last) {
            latchGet.countDown();
          }
        }

        @Override
        public void failure(int code) {
          System.err.println("Failed to get asset:" + code);
        }
      });
      Assert.assertTrue(latchGet.await(60000, TimeUnit.MILLISECONDS));
      Assert.assertEquals("This is a small file", new String(getResults.toByteArray(), StandardCharsets.UTF_8));
      ArrayList<String> ids = new ArrayList<>();
      CountDownLatch latch = new CountDownLatch(1);
      s3.listAssetsOf(new Key("space", key), new Callback<List<String>>() {
        @Override
        public void success(List<String> value) {
          System.err.println("Success:" + value.size());
          ids.addAll(value);
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
        }
      });
      Assert.assertTrue(latch.await(35000, TimeUnit.MILLISECONDS));
      Assert.assertEquals(1, ids.size());
      Assert.assertEquals(asset.id, ids.get(0));
      CountDownLatch latchDelete = new CountDownLatch(1);
      s3.deleteAsset(new Key("space", key), asset.id, new Callback<Void>() {
        @Override
        public void success(Void value) {
          latchDelete.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      Assert.assertTrue(latchDelete.await(30000, TimeUnit.MILLISECONDS));
    });
  }

  @Test
  public void bigAsset() throws Exception {
    flow((s3) -> {
      File tempAsset = File.createTempFile("ADAMATEST_", "temp");
      StringBuilder expected = new StringBuilder();
      for (int k = 0; k < 32 * 1024; k++) {
        expected.append("ABC-0123456789-XYZ");
      }
      Files.write(tempAsset.toPath(), expected.toString().getBytes(StandardCharsets.UTF_8));
      NtAsset asset = assetOf(tempAsset, "text/plain");
      CountDownLatch latchPut = new CountDownLatch(1);
      s3.upload(new Key("space", "key"), asset, AssetUploadBody.WRAP(tempAsset), new Callback<Void>() {
        @Override
        public void success(Void value) {
          latchPut.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          System.err.println("Failure:" + ex.code + "::" + ex.getMessage());
        }
      });
      Assert.assertTrue(latchPut.await(30000, TimeUnit.MILLISECONDS));
      for (int k = 0; k < 5; k++) {
        CountDownLatch latchGet = new CountDownLatch(2);
        ByteArrayOutputStream getResults = new ByteArrayOutputStream();
        s3.request(new AssetRequest("space", "key", asset.id), new AssetStream() {
          @Override
          public void headers(long length, String contentType, String md5) {
            Assert.assertEquals(asset.size, length);
            Assert.assertEquals(asset.contentType, contentType);
            Assert.assertEquals(asset.md5, md5);
            latchGet.countDown();
          }

          @Override
          public void body(byte[] chunk, int offset, int length, boolean last) {
            getResults.write(chunk, offset, length);
            if (last) {
              latchGet.countDown();
            }
          }

          @Override
          public void failure(int code) {
            System.err.println("Failed to get asset:" + code);
          }
        });
        Assert.assertTrue(latchGet.await(60000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(expected.toString(), new String(getResults.toByteArray(), StandardCharsets.UTF_8));
      }
      CountDownLatch latchDelete = new CountDownLatch(1);
      s3.deleteAsset(new Key("space", "key"), asset.id, new Callback<Void>() {
        @Override
        public void success(Void value) {
          latchDelete.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
        }
      });
      Assert.assertTrue(latchDelete.await(30000, TimeUnit.MILLISECONDS));
    });
  }

  @Test
  public void cloud() throws Exception {
    flow((s3) -> {
      Assert.assertTrue(s3.path().exists());
      String archiveKey = ProtectedUUID.generate();
      File root = new File(s3.path(), "space");
      root.mkdir();
      StringBuilder expected = new StringBuilder();
      for (int k = 0; k < 10; k++) {
        expected.append("This is an object to backup");
      }

      File archiveObject = new File(root, archiveKey);
      Files.writeString(archiveObject.toPath(), expected.toString());
      CountDownLatch latchBackup = new CountDownLatch(1);
      s3.backup(new Key("space", "key"), archiveObject, new Callback<Void>() {
        @Override
        public void success(Void value) {
          latchBackup.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
        }
      });
      Assert.assertTrue(latchBackup.await(30000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(archiveObject.delete());
      Assert.assertFalse(archiveObject.exists());
      CountDownLatch latchRestore = new CountDownLatch(1);
      s3.restore(new Key("space", "key"), archiveKey, new Callback<File>() {
        @Override
        public void success(File value) {
          Assert.assertTrue(value.exists());
          latchRestore.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      Assert.assertTrue(latchRestore.await(30000, TimeUnit.MILLISECONDS));
      CountDownLatch latchProtectBudget = new CountDownLatch(2);
      s3.backup(new Key("ide", null), 100, BackupService.Reason.Deployment, null, new Callback<Void>() {
        @Override
        public void success(Void value) {
          latchProtectBudget.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          Assert.fail();
        }
      });
      s3.backup(new Key("billing", null), 100, BackupService.Reason.Deployment, null, new Callback<Void>() {
        @Override
        public void success(Void value) {
          latchProtectBudget.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          Assert.fail();
        }
      });
      Assert.assertTrue(latchProtectBudget.await(50000, TimeUnit.MILLISECONDS));
      archiveObject = new File(root, archiveKey);
      Assert.assertTrue(archiveObject.exists());
      Assert.assertEquals(expected.toString(), Files.readString(archiveObject.toPath()));
      CountDownLatch latchDelete = new CountDownLatch(1);
      s3.delete(new Key("space", "key"), archiveKey, new Callback<Void>() {
        @Override
        public void success(Void value) {
          latchDelete.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      Assert.assertTrue(latchDelete.await(30000, TimeUnit.MILLISECONDS));
    });
  }

  @Test
  public void cloudBig() throws Exception {
    flow((s3) -> {
      Assert.assertTrue(s3.path().exists());
      String archiveKey = ProtectedUUID.generate();
      File root = new File(s3.path(), "space");
      root.mkdir();
      StringBuilder expected = new StringBuilder();
      for (int k = 0; k < 32 * 1024; k++) {
        expected.append("This is an object to backup");
      }

      File archiveObject = new File(root, archiveKey);
      Files.writeString(archiveObject.toPath(), expected.toString());
      CountDownLatch latchBackup = new CountDownLatch(1);
      s3.backup(new Key("space", "key"), archiveObject, new Callback<Void>() {
        @Override
        public void success(Void value) {
          latchBackup.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
        }
      });
      Assert.assertTrue(latchBackup.await(30000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(archiveObject.delete());
      Assert.assertFalse(archiveObject.exists());
      CountDownLatch latchRestore = new CountDownLatch(1);
      s3.restore(new Key("space", "key"), archiveKey, new Callback<File>() {
        @Override
        public void success(File value) {
          Assert.assertTrue(value.exists());
          latchRestore.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      Assert.assertTrue(latchRestore.await(30000, TimeUnit.MILLISECONDS));
      archiveObject = new File(root, archiveKey);
      Assert.assertTrue(archiveObject.exists());
      Assert.assertEquals(expected.toString(), Files.readString(archiveObject.toPath()));
      CountDownLatch latchDelete = new CountDownLatch(1);
      s3.delete(new Key("space", "key"), archiveKey, new Callback<Void>() {
        @Override
        public void success(Void value) {
          latchDelete.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      Assert.assertTrue(latchDelete.await(30000, TimeUnit.MILLISECONDS));
    });
  }

  @Test
  public void logs() throws Exception {
    // [a-z]*\.[0-9]*-[0-9]*-[0-9]*\.[0-9]*\.log
    flow((s3) -> {
      File log = new File(s3.path(), "mylog.123-123-123.123.log");
      Files.writeString(log.toPath(), "This is a sample log");
      Assert.assertTrue(log.exists());
      s3.uploadLogs(s3.path(), "my-prefix");
      for (int k = 0; k < 30 && log.exists(); k++) {
        Thread.sleep(100);
      }
      Assert.assertFalse(log.exists());
    });
  }
}
