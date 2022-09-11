/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.extern.aws;

import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.AssetRequest;
import org.adamalang.web.assets.AssetStream;
import org.adamalang.web.assets.AssetUploadBody;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.service.WebConfig;
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
    WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(Json.newJsonObject())));
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
        public void headers(long length, String contentType) {
          Assert.assertEquals(asset.size, length);
          Assert.assertEquals(asset.contentType, contentType);
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
      s3.listAssets(new Key("space", key), new Callback<List<String>>() {
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
      CountDownLatch latchGet = new CountDownLatch(2);
      ByteArrayOutputStream getResults = new ByteArrayOutputStream();
      s3.request(new AssetRequest("space", "key", asset.id), new AssetStream() {
        @Override
        public void headers(long length, String contentType) {
          Assert.assertEquals(asset.size, length);
          Assert.assertEquals(asset.contentType, contentType);
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
