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
      store.append(42, "XYZ".getBytes(StandardCharsets.UTF_8), event);
      Assert.assertTrue(store.exists(42));
      for (int k = 0 ; k < 100; k++) {
        store.append(1, ("K:" + k).getBytes(StandardCharsets.UTF_8), event);
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
      Assert.assertTrue(store.trim(1, 90, event));
      Assert.assertFalse(store.trim(1, -1, event));
      {
        MockByteArrayStream stream = new MockByteArrayStream();
        store.read(1, stream);
        stream.assertIs("[0=K:90][1=K:91][2=K:92][3=K:93][4=K:94][5=K:95][6=K:96][7=K:97][8=K:98][9=K:99]FINISHED");
      }
      store.delete(1, event);
      store.flush(false);
      Assert.assertEquals(104, count.get()); // 1 PUT, 100 PUT, 1 DELETE, 1 TRIM, 1 DELETE
      for (int k = 0; k < 1000; k++) {
        store.append(k, ("K:" + k).getBytes(StandardCharsets.UTF_8), event);
      }
      for (int k = 0; k < 1000; k++) {
        Assert.assertTrue(store.delete(k, event));
      }
      store.flush(true);
      Assert.assertFalse(store.delete(102312412L, event));
      Assert.assertEquals(2104, count.get()); // + 1000 PUT, 1000 DELETE

      int fill = 0;
      while (store.append(1, ("01234567890123456789012345678901234567890123456789Junk@" + fill).getBytes(StandardCharsets.UTF_8), event) != null) {
        fill++;
      }
      Assert.assertFalse(store.append(100, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX".getBytes(StandardCharsets.UTF_8), event) != null);
      Assert.assertEquals(3233, count.get());
      store.delete(1, event);
      store.flush(false);
      Assert.assertEquals(3234, count.get());
      Assert.assertTrue(store.append(100, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX".getBytes(StandardCharsets.UTF_8), event) != null);
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
        store.append(42, "XYZ".getBytes(StandardCharsets.UTF_8), event);
        for (int k = 0 ; k < 100; k++) {
          store.append(1, ("K:" + k).getBytes(StandardCharsets.UTF_8), event);
          store.append(2, ("K:" + k).getBytes(StandardCharsets.UTF_8), event);
          store.append(3, ("K:" + k).getBytes(StandardCharsets.UTF_8), event);
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
