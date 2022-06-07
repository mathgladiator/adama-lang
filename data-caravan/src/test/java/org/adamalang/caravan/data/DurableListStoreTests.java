/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.caravan.data;

import org.adamalang.caravan.mocks.MockByteArrayStream;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DurableListStoreTests {
  @Test
  public void flow() throws Exception {
    File testRoot = File.createTempFile("flow", "durable_list_store_flow");
    testRoot.delete();
    testRoot.mkdirs();
    try {
      // public DurableListStore(File storeFile, File walRoot, long size, int initialWriteBufferSize, int flushCutOffBytes, long maxLogSize) throws IOException {
      DurableListStore store = new DurableListStore(new DurableListStoreMetrics(new NoOpMetricsFactory()), new File(testRoot, "storage"), testRoot, 64 * 1024,  64, 1024);
      AtomicInteger count = new AtomicInteger(0);
      Runnable event = () -> { count.incrementAndGet(); };
      Assert.assertFalse(store.exists(42));
      store.append(42, "XYZ".getBytes(StandardCharsets.UTF_8), 0, 42L, event);
      Assert.assertTrue(store.exists(42));
      for (int k = 0 ; k < 100; k++) {
        store.append(1, ("K:" + k).getBytes(StandardCharsets.UTF_8), k + 1, 100L, event);
      }
      {
        MockByteArrayStream stream = new MockByteArrayStream();
        store.read(42, stream);
        stream.assertIs("[0=XYZ]FINISHED");
      }
      store.delete(42, event);
      Assert.assertFalse(store.exists(42));
      {
        MockByteArrayStream stream = new MockByteArrayStream();
        store.read(1, stream);
        stream.assertIs("[0=K:0][1=K:1][2=K:2][3=K:3][4=K:4][5=K:5][6=K:6][7=K:7][8=K:8][9=K:9][10=K:10][11=K:11][12=K:12][13=K:13][14=K:14][15=K:15][16=K:16][17=K:17][18=K:18][19=K:19][20=K:20][21=K:21][22=K:22][23=K:23][24=K:24][25=K:25][26=K:26][27=K:27][28=K:28][29=K:29][30=K:30][31=K:31][32=K:32][33=K:33][34=K:34][35=K:35][36=K:36][37=K:37][38=K:38][39=K:39][40=K:40][41=K:41][42=K:42][43=K:43][44=K:44][45=K:45][46=K:46][47=K:47][48=K:48][49=K:49][50=K:50][51=K:51][52=K:52][53=K:53][54=K:54][55=K:55][56=K:56][57=K:57][58=K:58][59=K:59][60=K:60][61=K:61][62=K:62][63=K:63][64=K:64][65=K:65][66=K:66][67=K:67][68=K:68][69=K:69][70=K:70][71=K:71][72=K:72][73=K:73][74=K:74][75=K:75][76=K:76][77=K:77][78=K:78][79=K:79][80=K:80][81=K:81][82=K:82][83=K:83][84=K:84][85=K:85][86=K:86][87=K:87][88=K:88][89=K:89][90=K:90][91=K:91][92=K:92][93=K:93][94=K:94][95=K:95][96=K:96][97=K:97][98=K:98][99=K:99]FINISHED");
      }
      Assert.assertEquals(65146, store.available());
      Assert.assertTrue(store.trim(1, 90, event));
      Assert.assertFalse(store.trim(1, -1, event));
      {
        MockByteArrayStream stream = new MockByteArrayStream();
        store.read(1, stream);
        stream.assertIs("[0=K:90][1=K:91][2=K:92][3=K:93][4=K:94][5=K:95][6=K:96][7=K:97][8=K:98][9=K:99]FINISHED");
      }
      Assert.assertEquals(65496, store.available());
      store.delete(1, event);
      store.flush(false);
      Assert.assertEquals(104, count.get()); // 1 PUT, 100 PUT, 1 DELETE, 1 TRIM, 1 DELETE
      for (int k = 0; k < 1000; k++) {
        store.append(k, ("K:" + k).getBytes(StandardCharsets.UTF_8), k, 1024, event);
      }
      for (int k = 0; k < 1000; k++) {
        Assert.assertTrue(store.delete(k, event));
      }
      store.flush(true);
      Assert.assertFalse(store.delete(102312412L, event));
      Assert.assertEquals(2104, count.get()); // + 1000 PUT, 1000 DELETE
      Assert.assertEquals(65536, store.available());
      int fill = 0;
      while (store.append(1, ("01234567890123456789012345678901234567890123456789Junk@" + fill).getBytes(StandardCharsets.UTF_8), 0, 0, event) != null) {
        fill++;
      }
      Assert.assertFalse(store.append(100, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX".getBytes(StandardCharsets.UTF_8), 0, 1024, event) != null);
      Assert.assertEquals(3233, count.get());
      store.delete(1, event);
      store.flush(false);
      Assert.assertEquals(3234, count.get());
      Assert.assertTrue(store.append(100, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX".getBytes(StandardCharsets.UTF_8), 1, 128, event) != null);
      store.flush(false);
      Assert.assertEquals(3235, count.get());
      store.shutdown();
    } finally {
      for (File file : testRoot.listFiles()) {
        file.delete();
      }
      testRoot.delete();
    }
  }

  @Test
  public void batch() throws Exception {
    File testRoot = File.createTempFile("flow", "durable_list_store_batch");
    testRoot.delete();
    testRoot.mkdirs();
    try {
      // public DurableListStore(File storeFile, File walRoot, long size, int initialWriteBufferSize, int flushCutOffBytes, long maxLogSize) throws IOException {
      DurableListStore store = new DurableListStore(new DurableListStoreMetrics(new NoOpMetricsFactory()), new File(testRoot, "storage"), testRoot, 64 * 1024, 64, 1024);
      AtomicInteger count = new AtomicInteger(0);
      Runnable event = () -> {
        count.incrementAndGet();
      };
      Assert.assertFalse(store.exists(42));
      ArrayList<byte[]> batch1 = new ArrayList<>();
      batch1.add("XYZ".getBytes(StandardCharsets.UTF_8));
      for (int k = 0; k < 100; k++) {
        batch1.add(("K:" + k).getBytes(StandardCharsets.UTF_8));
      }
      Assert.assertEquals(65536, store.available());
      store.append(42, batch1, 10, 1024, event);
      Assert.assertEquals(65143, store.available());
      store.flush(false);
      {
        MockByteArrayStream stream = new MockByteArrayStream();
        store.read(42, stream);
        stream.assertIs("[0=XYZ][1=K:0][2=K:1][3=K:2][4=K:3][5=K:4][6=K:5][7=K:6][8=K:7][9=K:8][10=K:9][11=K:10][12=K:11][13=K:12][14=K:13][15=K:14][16=K:15][17=K:16][18=K:17][19=K:18][20=K:19][21=K:20][22=K:21][23=K:22][24=K:23][25=K:24][26=K:25][27=K:26][28=K:27][29=K:28][30=K:29][31=K:30][32=K:31][33=K:32][34=K:33][35=K:34][36=K:35][37=K:36][38=K:37][39=K:38][40=K:39][41=K:40][42=K:41][43=K:42][44=K:43][45=K:44][46=K:45][47=K:46][48=K:47][49=K:48][50=K:49][51=K:50][52=K:51][53=K:52][54=K:53][55=K:54][56=K:55][57=K:56][58=K:57][59=K:58][60=K:59][61=K:60][62=K:61][63=K:62][64=K:63][65=K:64][66=K:65][67=K:66][68=K:67][69=K:68][70=K:69][71=K:70][72=K:71][73=K:72][74=K:73][75=K:74][76=K:75][77=K:76][78=K:77][79=K:78][80=K:79][81=K:80][82=K:81][83=K:82][84=K:83][85=K:84][86=K:85][87=K:86][88=K:87][89=K:88][90=K:89][91=K:90][92=K:91][93=K:92][94=K:93][95=K:94][96=K:95][97=K:96][98=K:97][99=K:98][100=K:99]FINISHED");
      }
      {
        ArrayList<byte[]> batch2 = new ArrayList<>();
        batch2.add("XYZ".getBytes(StandardCharsets.UTF_8));
        for (int k = 0; k < 7 * 1024; k++) {
          batch2.add(("K:" + k).getBytes(StandardCharsets.UTF_8));
        }
        Assert.assertEquals(7169, (int) store.append(100, batch2, 10, 1024, event));
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(100, stream);
          stream.assertLength(90982);
        }
      }
      Assert.assertEquals(23242, store.available());
      {
        ArrayList<byte[]> batch3 = new ArrayList<>();
        for (int k = 0; k < 7 * 1024; k++) {
          batch3.add(("K:" + k).getBytes(StandardCharsets.UTF_8));
        }
        Assert.assertNull(store.append(100, batch3, 10, 1024, event));
      }
      Assert.assertEquals(23242, store.available());
      Assert.assertTrue(store.trim(100, 10000, () -> {}));
      Assert.assertEquals(65143, store.available());
      Assert.assertTrue(store.trim(42, 10000, () -> {}));
      Assert.assertEquals(65536, store.available());
      store.shutdown();
    } finally {
      for (File file : testRoot.listFiles()) {
        file.delete();
      }
      testRoot.delete();
    }
  }

  @Test
  public void load() throws Exception {
    File testRoot = File.createTempFile("flow", "durable_list_store_load");
    testRoot.delete();
    testRoot.mkdirs();
    try {
      // public DurableListStore(File storeFile, File walRoot, long size, int initialWriteBufferSize, int flushCutOffBytes, long maxLogSize) throws IOException {
      {
        DurableListStore store = new DurableListStore(new DurableListStoreMetrics(new NoOpMetricsFactory()), new File(testRoot, "storage"), testRoot, 64 * 1024, 1024 * 1024, 32 * 1024 * 1024);
        AtomicInteger count = new AtomicInteger(0);
        Runnable event = () -> { count.incrementAndGet(); };
        store.append(42, "XYZ".getBytes(StandardCharsets.UTF_8), 10, 1024, event);
        for (int k = 0 ; k < 100; k++) {
          store.append(1, ("K:" + k).getBytes(StandardCharsets.UTF_8), 10, 1024, event);
          store.append(2, ("K:" + k).getBytes(StandardCharsets.UTF_8), 10, 1024, event);
          store.append(3, ("K:" + k).getBytes(StandardCharsets.UTF_8), 10, 1024, event);
        }

        store.trim(2, 90, event);
        Assert.assertTrue(store.exists(3));
        store.delete(3, event);
        Assert.assertFalse(store.exists(3));
        store.flush(false);
        store.shutdown();
      }
      {
        DurableListStore store = new DurableListStore(new DurableListStoreMetrics(new NoOpMetricsFactory()), new File(testRoot, "storage-two"), testRoot, 64 * 1024, 1024 * 1024, 32 * 1024 * 1024);
        AtomicInteger count = new AtomicInteger(0);
        Runnable event = () -> { count.incrementAndGet(); };
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(42, stream);
          stream.assertIs("[0=XYZ]FINISHED");
        }
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(1, stream);
          stream.assertIs("[0=K:0][1=K:1][2=K:2][3=K:3][4=K:4][5=K:5][6=K:6][7=K:7][8=K:8][9=K:9][10=K:10][11=K:11][12=K:12][13=K:13][14=K:14][15=K:15][16=K:16][17=K:17][18=K:18][19=K:19][20=K:20][21=K:21][22=K:22][23=K:23][24=K:24][25=K:25][26=K:26][27=K:27][28=K:28][29=K:29][30=K:30][31=K:31][32=K:32][33=K:33][34=K:34][35=K:35][36=K:36][37=K:37][38=K:38][39=K:39][40=K:40][41=K:41][42=K:42][43=K:43][44=K:44][45=K:45][46=K:46][47=K:47][48=K:48][49=K:49][50=K:50][51=K:51][52=K:52][53=K:53][54=K:54][55=K:55][56=K:56][57=K:57][58=K:58][59=K:59][60=K:60][61=K:61][62=K:62][63=K:63][64=K:64][65=K:65][66=K:66][67=K:67][68=K:68][69=K:69][70=K:70][71=K:71][72=K:72][73=K:73][74=K:74][75=K:75][76=K:76][77=K:77][78=K:78][79=K:79][80=K:80][81=K:81][82=K:82][83=K:83][84=K:84][85=K:85][86=K:86][87=K:87][88=K:88][89=K:89][90=K:90][91=K:91][92=K:92][93=K:93][94=K:94][95=K:95][96=K:96][97=K:97][98=K:98][99=K:99]FINISHED");
        }
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(2, stream);
          stream.assertIs("[0=K:90][1=K:91][2=K:92][3=K:93][4=K:94][5=K:95][6=K:96][7=K:97][8=K:98][9=K:99]FINISHED");
        }
        Assert.assertFalse(store.exists(3));
        store.flush(true);
        store.shutdown();
      }
      {
        DurableListStore store = new DurableListStore(new DurableListStoreMetrics(new NoOpMetricsFactory()), new File(testRoot, "storage"), testRoot, 64 * 1024, 1024 * 1024, 32 * 1024 * 1024);
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(42, stream);
          stream.assertIs("[0=XYZ]FINISHED");
        }
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(1, stream);
          stream.assertIs("[0=K:0][1=K:1][2=K:2][3=K:3][4=K:4][5=K:5][6=K:6][7=K:7][8=K:8][9=K:9][10=K:10][11=K:11][12=K:12][13=K:13][14=K:14][15=K:15][16=K:16][17=K:17][18=K:18][19=K:19][20=K:20][21=K:21][22=K:22][23=K:23][24=K:24][25=K:25][26=K:26][27=K:27][28=K:28][29=K:29][30=K:30][31=K:31][32=K:32][33=K:33][34=K:34][35=K:35][36=K:36][37=K:37][38=K:38][39=K:39][40=K:40][41=K:41][42=K:42][43=K:43][44=K:44][45=K:45][46=K:46][47=K:47][48=K:48][49=K:49][50=K:50][51=K:51][52=K:52][53=K:53][54=K:54][55=K:55][56=K:56][57=K:57][58=K:58][59=K:59][60=K:60][61=K:61][62=K:62][63=K:63][64=K:64][65=K:65][66=K:66][67=K:67][68=K:68][69=K:69][70=K:70][71=K:71][72=K:72][73=K:73][74=K:74][75=K:75][76=K:76][77=K:77][78=K:78][79=K:79][80=K:80][81=K:81][82=K:82][83=K:83][84=K:84][85=K:85][86=K:86][87=K:87][88=K:88][89=K:89][90=K:90][91=K:91][92=K:92][93=K:93][94=K:94][95=K:95][96=K:96][97=K:97][98=K:98][99=K:99]FINISHED");
        }
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(2, stream);
          stream.assertIs("[0=K:90][1=K:91][2=K:92][3=K:93][4=K:94][5=K:95][6=K:96][7=K:97][8=K:98][9=K:99]FINISHED");
        }
        store.shutdown();
      }

    } finally {
      for (File file : testRoot.listFiles()) {
        file.delete();
      }
      testRoot.delete();
    }
  }
}
