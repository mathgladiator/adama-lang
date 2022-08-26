/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.contracts.ByteArrayStream;
import org.adamalang.caravan.events.EventCodec;
import org.adamalang.caravan.events.Events;
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
      DurableListStore store = new DurableListStore(new DiskMetrics(new NoOpMetricsFactory()), new File(testRoot, "storage"), testRoot, 64 * 1024,  64, 1024);
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
        stream.assertIs("[0=XYZ/0:42]FINISHED");
      }
      store.delete(42, event);
      Assert.assertFalse(store.exists(42));
      {
        MockByteArrayStream stream = new MockByteArrayStream();
        store.read(1, stream);
        stream.assertIs("[0=K:0/1:100][1=K:1/2:100][2=K:2/3:100][3=K:3/4:100][4=K:4/5:100][5=K:5/6:100][6=K:6/7:100][7=K:7/8:100][8=K:8/9:100][9=K:9/10:100][10=K:10/11:100][11=K:11/12:100][12=K:12/13:100][13=K:13/14:100][14=K:14/15:100][15=K:15/16:100][16=K:16/17:100][17=K:17/18:100][18=K:18/19:100][19=K:19/20:100][20=K:20/21:100][21=K:21/22:100][22=K:22/23:100][23=K:23/24:100][24=K:24/25:100][25=K:25/26:100][26=K:26/27:100][27=K:27/28:100][28=K:28/29:100][29=K:29/30:100][30=K:30/31:100][31=K:31/32:100][32=K:32/33:100][33=K:33/34:100][34=K:34/35:100][35=K:35/36:100][36=K:36/37:100][37=K:37/38:100][38=K:38/39:100][39=K:39/40:100][40=K:40/41:100][41=K:41/42:100][42=K:42/43:100][43=K:43/44:100][44=K:44/45:100][45=K:45/46:100][46=K:46/47:100][47=K:47/48:100][48=K:48/49:100][49=K:49/50:100][50=K:50/51:100][51=K:51/52:100][52=K:52/53:100][53=K:53/54:100][54=K:54/55:100][55=K:55/56:100][56=K:56/57:100][57=K:57/58:100][58=K:58/59:100][59=K:59/60:100][60=K:60/61:100][61=K:61/62:100][62=K:62/63:100][63=K:63/64:100][64=K:64/65:100][65=K:65/66:100][66=K:66/67:100][67=K:67/68:100][68=K:68/69:100][69=K:69/70:100][70=K:70/71:100][71=K:71/72:100][72=K:72/73:100][73=K:73/74:100][74=K:74/75:100][75=K:75/76:100][76=K:76/77:100][77=K:77/78:100][78=K:78/79:100][79=K:79/80:100][80=K:80/81:100][81=K:81/82:100][82=K:82/83:100][83=K:83/84:100][84=K:84/85:100][85=K:85/86:100][86=K:86/87:100][87=K:87/88:100][88=K:88/89:100][89=K:89/90:100][90=K:90/91:100][91=K:91/92:100][92=K:92/93:100][93=K:93/94:100][94=K:94/95:100][95=K:95/96:100][96=K:96/97:100][97=K:97/98:100][98=K:98/99:100][99=K:99/100:100]FINISHED");
      }
      Assert.assertEquals(65146, store.available());
      Assert.assertTrue(store.trim(1, 90, event));
      Assert.assertFalse(store.trim(1, -1, event));
      {
        MockByteArrayStream stream = new MockByteArrayStream();
        store.read(1, stream);
        stream.assertIs("[0=K:10/11:100][1=K:11/12:100][2=K:12/13:100][3=K:13/14:100][4=K:14/15:100][5=K:15/16:100][6=K:16/17:100][7=K:17/18:100][8=K:18/19:100][9=K:19/20:100][10=K:20/21:100][11=K:21/22:100][12=K:22/23:100][13=K:23/24:100][14=K:24/25:100][15=K:25/26:100][16=K:26/27:100][17=K:27/28:100][18=K:28/29:100][19=K:29/30:100][20=K:30/31:100][21=K:31/32:100][22=K:32/33:100][23=K:33/34:100][24=K:34/35:100][25=K:35/36:100][26=K:36/37:100][27=K:37/38:100][28=K:38/39:100][29=K:39/40:100][30=K:40/41:100][31=K:41/42:100][32=K:42/43:100][33=K:43/44:100][34=K:44/45:100][35=K:45/46:100][36=K:46/47:100][37=K:47/48:100][38=K:48/49:100][39=K:49/50:100][40=K:50/51:100][41=K:51/52:100][42=K:52/53:100][43=K:53/54:100][44=K:54/55:100][45=K:55/56:100][46=K:56/57:100][47=K:57/58:100][48=K:58/59:100][49=K:59/60:100][50=K:60/61:100][51=K:61/62:100][52=K:62/63:100][53=K:63/64:100][54=K:64/65:100][55=K:65/66:100][56=K:66/67:100][57=K:67/68:100][58=K:68/69:100][59=K:69/70:100][60=K:70/71:100][61=K:71/72:100][62=K:72/73:100][63=K:73/74:100][64=K:74/75:100][65=K:75/76:100][66=K:76/77:100][67=K:77/78:100][68=K:78/79:100][69=K:79/80:100][70=K:80/81:100][71=K:81/82:100][72=K:82/83:100][73=K:83/84:100][74=K:84/85:100][75=K:85/86:100][76=K:86/87:100][77=K:87/88:100][78=K:88/89:100][79=K:89/90:100][80=K:90/91:100][81=K:91/92:100][82=K:92/93:100][83=K:93/94:100][84=K:94/95:100][85=K:95/96:100][86=K:96/97:100][87=K:97/98:100][88=K:98/99:100][89=K:99/100:100]FINISHED");
      }
      Assert.assertEquals(65176, store.available());
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

  private byte[] encode(String xyz) {
    Events.Change change = new Events.Change();
    change.redo = xyz;
    change.undo = "";
    change.seq_begin = 1;
    change.seq_end = 2;
    change.dAssetBytes = 42;
    change.authority = "dev";
    change.agent = "me";
    change.request = "";
    ByteBuf buf = Unpooled.buffer();
    EventCodec.write(buf, change);
    byte[] chunk = new byte[buf.readableBytes()];
    buf.readBytes(chunk);
    return chunk;
  }

  private ByteArrayStream wrap(ByteArrayStream stream) {
    return new ByteArrayStream() {
      @Override
      public void next(int appendIndex, byte[] value, int seq, long assetBytes) throws Exception {
        EventCodec.route(Unpooled.wrappedBuffer(value), new EventCodec.HandlerEvent() {
          @Override
          public void handle(Events.Snapshot payload) {

          }

          @Override
          public void handle(Events.Batch payload) {

          }

          @Override
          public void handle(Events.Change payload) {
            try {
              stream.next(appendIndex, payload.redo.getBytes(StandardCharsets.UTF_8), seq, assetBytes);
            } catch (Exception ex) {
              throw new RuntimeException(ex);
            }
          }
        });
      }

      @Override
      public void finished() throws Exception {
        stream.finished();
      }
    };
  }

  @Test
  public void batch() throws Exception {
    File testRoot = File.createTempFile("flow", "durable_list_store_batch");
    testRoot.delete();
    testRoot.mkdirs();
    try {
      DurableListStore store = new DurableListStore(new DiskMetrics(new NoOpMetricsFactory()), new File(testRoot, "storage"), testRoot, 64 * 1024, 64, 1024);
      AtomicInteger count = new AtomicInteger(0);
      Runnable event = () -> {
        count.incrementAndGet();
      };
      Assert.assertFalse(store.exists(42));
      ArrayList<byte[]> batch1 = new ArrayList<>();
      batch1.add(encode("XYZ"));
      for (int k = 0; k < 100; k++) {
        batch1.add(encode(("K:" + k)));
      }
      Assert.assertEquals(65536, store.available());
      store.append(42, batch1, 10, 1024, event);
      Assert.assertEquals(60093, store.available());
      store.flush(false);
      {
        MockByteArrayStream stream = new MockByteArrayStream();
        store.read(42, wrap(stream));
        stream.assertIs("[0=XYZ/2:42][1=K:0/2:42][2=K:1/2:42][3=K:2/2:42][4=K:3/2:42][5=K:4/2:42][6=K:5/2:42][7=K:6/2:42][8=K:7/2:42][9=K:8/2:42][10=K:9/2:42][11=K:10/2:42][12=K:11/2:42][13=K:12/2:42][14=K:13/2:42][15=K:14/2:42][16=K:15/2:42][17=K:16/2:42][18=K:17/2:42][19=K:18/2:42][20=K:19/2:42][21=K:20/2:42][22=K:21/2:42][23=K:22/2:42][24=K:23/2:42][25=K:24/2:42][26=K:25/2:42][27=K:26/2:42][28=K:27/2:42][29=K:28/2:42][30=K:29/2:42][31=K:30/2:42][32=K:31/2:42][33=K:32/2:42][34=K:33/2:42][35=K:34/2:42][36=K:35/2:42][37=K:36/2:42][38=K:37/2:42][39=K:38/2:42][40=K:39/2:42][41=K:40/2:42][42=K:41/2:42][43=K:42/2:42][44=K:43/2:42][45=K:44/2:42][46=K:45/2:42][47=K:46/2:42][48=K:47/2:42][49=K:48/2:42][50=K:49/2:42][51=K:50/2:42][52=K:51/2:42][53=K:52/2:42][54=K:53/2:42][55=K:54/2:42][56=K:55/2:42][57=K:56/2:42][58=K:57/2:42][59=K:58/2:42][60=K:59/2:42][61=K:60/2:42][62=K:61/2:42][63=K:62/2:42][64=K:63/2:42][65=K:64/2:42][66=K:65/2:42][67=K:66/2:42][68=K:67/2:42][69=K:68/2:42][70=K:69/2:42][71=K:70/2:42][72=K:71/2:42][73=K:72/2:42][74=K:73/2:42][75=K:74/2:42][76=K:75/2:42][77=K:76/2:42][78=K:77/2:42][79=K:78/2:42][80=K:79/2:42][81=K:80/2:42][82=K:81/2:42][83=K:82/2:42][84=K:83/2:42][85=K:84/2:42][86=K:85/2:42][87=K:86/2:42][88=K:87/2:42][89=K:88/2:42][90=K:89/2:42][91=K:90/2:42][92=K:91/2:42][93=K:92/2:42][94=K:93/2:42][95=K:94/2:42][96=K:95/2:42][97=K:96/2:42][98=K:97/2:42][99=K:98/2:42][100=K:99/2:42]FINISHED");
      }
      {
        ArrayList<byte[]> batch2 = new ArrayList<>();
        batch2.add(encode("XYZ"));
        for (int k = 0; k < 1024; k++) {
          batch2.add(encode("K:" + k));
        }
        Assert.assertEquals(1025, (int) store.append(100, batch2, 10, 1024, event));
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(100, wrap(stream));
          stream.assertLength(16235);
        }
      }
      Assert.assertEquals(3806, store.available());
      {
        ArrayList<byte[]> batch3 = new ArrayList<>();
        for (int k = 0; k < 7 * 1024; k++) {
          batch3.add(encode("K:" + k));
        }
        Assert.assertNull(store.append(100, batch3, 10, 1024, event));
      }
      Assert.assertEquals(3806, store.available());
      Assert.assertTrue(store.trim(100, 1000, () -> {}));
      Assert.assertEquals(5145, store.available());
      Assert.assertTrue(store.trim(42, 1, () -> {}));
      Assert.assertEquals(10534, store.available());
      Assert.assertTrue(store.trim(100, 10, () -> {}));
      Assert.assertEquals(64922, store.available());
      Assert.assertTrue(store.trim(100, 1, () -> {}));
      Assert.assertEquals(65426, store.available());
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
        DurableListStore store = new DurableListStore(new DiskMetrics(new NoOpMetricsFactory()), new File(testRoot, "storage"), testRoot, 64 * 1024, 1024 * 1024, 32 * 1024 * 1024);
        AtomicInteger count = new AtomicInteger(0);
        Runnable event = () -> { count.incrementAndGet(); };
        store.append(42, "XYZ".getBytes(StandardCharsets.UTF_8), 10, 1024, event);
        for (int k = 0 ; k < 100; k++) {
          store.append(1, ("K:" + k).getBytes(StandardCharsets.UTF_8), 10, 1024, event);
          store.append(2, ("K:" + k).getBytes(StandardCharsets.UTF_8), 10, 1024, event);
          store.append(3, ("K:" + k).getBytes(StandardCharsets.UTF_8), 10, 1024, event);
        }

        store.trim(2, 5, event);
        Assert.assertTrue(store.exists(3));
        store.delete(3, event);
        Assert.assertFalse(store.exists(3));
        store.flush(false);
        store.shutdown();
      }
      {
        DurableListStore store = new DurableListStore(new DiskMetrics(new NoOpMetricsFactory()), new File(testRoot, "storage-two"), testRoot, 64 * 1024, 1024 * 1024, 32 * 1024 * 1024);
        AtomicInteger count = new AtomicInteger(0);
        Runnable event = () -> { count.incrementAndGet(); };
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(42, stream);
          stream.assertIs("[0=XYZ/10:1024]FINISHED");
        }
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(1, stream);
          stream.assertIs("[0=K:0/10:1024][1=K:1/10:1024][2=K:2/10:1024][3=K:3/10:1024][4=K:4/10:1024][5=K:5/10:1024][6=K:6/10:1024][7=K:7/10:1024][8=K:8/10:1024][9=K:9/10:1024][10=K:10/10:1024][11=K:11/10:1024][12=K:12/10:1024][13=K:13/10:1024][14=K:14/10:1024][15=K:15/10:1024][16=K:16/10:1024][17=K:17/10:1024][18=K:18/10:1024][19=K:19/10:1024][20=K:20/10:1024][21=K:21/10:1024][22=K:22/10:1024][23=K:23/10:1024][24=K:24/10:1024][25=K:25/10:1024][26=K:26/10:1024][27=K:27/10:1024][28=K:28/10:1024][29=K:29/10:1024][30=K:30/10:1024][31=K:31/10:1024][32=K:32/10:1024][33=K:33/10:1024][34=K:34/10:1024][35=K:35/10:1024][36=K:36/10:1024][37=K:37/10:1024][38=K:38/10:1024][39=K:39/10:1024][40=K:40/10:1024][41=K:41/10:1024][42=K:42/10:1024][43=K:43/10:1024][44=K:44/10:1024][45=K:45/10:1024][46=K:46/10:1024][47=K:47/10:1024][48=K:48/10:1024][49=K:49/10:1024][50=K:50/10:1024][51=K:51/10:1024][52=K:52/10:1024][53=K:53/10:1024][54=K:54/10:1024][55=K:55/10:1024][56=K:56/10:1024][57=K:57/10:1024][58=K:58/10:1024][59=K:59/10:1024][60=K:60/10:1024][61=K:61/10:1024][62=K:62/10:1024][63=K:63/10:1024][64=K:64/10:1024][65=K:65/10:1024][66=K:66/10:1024][67=K:67/10:1024][68=K:68/10:1024][69=K:69/10:1024][70=K:70/10:1024][71=K:71/10:1024][72=K:72/10:1024][73=K:73/10:1024][74=K:74/10:1024][75=K:75/10:1024][76=K:76/10:1024][77=K:77/10:1024][78=K:78/10:1024][79=K:79/10:1024][80=K:80/10:1024][81=K:81/10:1024][82=K:82/10:1024][83=K:83/10:1024][84=K:84/10:1024][85=K:85/10:1024][86=K:86/10:1024][87=K:87/10:1024][88=K:88/10:1024][89=K:89/10:1024][90=K:90/10:1024][91=K:91/10:1024][92=K:92/10:1024][93=K:93/10:1024][94=K:94/10:1024][95=K:95/10:1024][96=K:96/10:1024][97=K:97/10:1024][98=K:98/10:1024][99=K:99/10:1024]FINISHED");
        }
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(2, stream);
          stream.assertIs("[0=K:95/10:1024][1=K:96/10:1024][2=K:97/10:1024][3=K:98/10:1024][4=K:99/10:1024]FINISHED");
        }
        Assert.assertFalse(store.exists(3));
        store.flush(true);
        store.shutdown();
      }
      {
        DurableListStore store = new DurableListStore(new DiskMetrics(new NoOpMetricsFactory()), new File(testRoot, "storage"), testRoot, 64 * 1024, 1024 * 1024, 32 * 1024 * 1024);
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(42, stream);
          stream.assertIs("[0=XYZ/10:1024]FINISHED");
        }
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(1, stream);
          stream.assertIs("[0=K:0/10:1024][1=K:1/10:1024][2=K:2/10:1024][3=K:3/10:1024][4=K:4/10:1024][5=K:5/10:1024][6=K:6/10:1024][7=K:7/10:1024][8=K:8/10:1024][9=K:9/10:1024][10=K:10/10:1024][11=K:11/10:1024][12=K:12/10:1024][13=K:13/10:1024][14=K:14/10:1024][15=K:15/10:1024][16=K:16/10:1024][17=K:17/10:1024][18=K:18/10:1024][19=K:19/10:1024][20=K:20/10:1024][21=K:21/10:1024][22=K:22/10:1024][23=K:23/10:1024][24=K:24/10:1024][25=K:25/10:1024][26=K:26/10:1024][27=K:27/10:1024][28=K:28/10:1024][29=K:29/10:1024][30=K:30/10:1024][31=K:31/10:1024][32=K:32/10:1024][33=K:33/10:1024][34=K:34/10:1024][35=K:35/10:1024][36=K:36/10:1024][37=K:37/10:1024][38=K:38/10:1024][39=K:39/10:1024][40=K:40/10:1024][41=K:41/10:1024][42=K:42/10:1024][43=K:43/10:1024][44=K:44/10:1024][45=K:45/10:1024][46=K:46/10:1024][47=K:47/10:1024][48=K:48/10:1024][49=K:49/10:1024][50=K:50/10:1024][51=K:51/10:1024][52=K:52/10:1024][53=K:53/10:1024][54=K:54/10:1024][55=K:55/10:1024][56=K:56/10:1024][57=K:57/10:1024][58=K:58/10:1024][59=K:59/10:1024][60=K:60/10:1024][61=K:61/10:1024][62=K:62/10:1024][63=K:63/10:1024][64=K:64/10:1024][65=K:65/10:1024][66=K:66/10:1024][67=K:67/10:1024][68=K:68/10:1024][69=K:69/10:1024][70=K:70/10:1024][71=K:71/10:1024][72=K:72/10:1024][73=K:73/10:1024][74=K:74/10:1024][75=K:75/10:1024][76=K:76/10:1024][77=K:77/10:1024][78=K:78/10:1024][79=K:79/10:1024][80=K:80/10:1024][81=K:81/10:1024][82=K:82/10:1024][83=K:83/10:1024][84=K:84/10:1024][85=K:85/10:1024][86=K:86/10:1024][87=K:87/10:1024][88=K:88/10:1024][89=K:89/10:1024][90=K:90/10:1024][91=K:91/10:1024][92=K:92/10:1024][93=K:93/10:1024][94=K:94/10:1024][95=K:95/10:1024][96=K:96/10:1024][97=K:97/10:1024][98=K:98/10:1024][99=K:99/10:1024]FINISHED");
        }
        {
          MockByteArrayStream stream = new MockByteArrayStream();
          store.read(2, stream);
          stream.assertIs("[0=K:95/10:1024][1=K:96/10:1024][2=K:97/10:1024][3=K:98/10:1024][4=K:99/10:1024]FINISHED");
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
