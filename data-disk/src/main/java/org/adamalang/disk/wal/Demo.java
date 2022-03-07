/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.wal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Demo {
  public static class Sample {
    public int latency;
    public long bytesWritten;
    public int messages;
  }
  public static Sample sample(File root, int cutoff, long bytesToWrite) throws Exception {
    long bytesWritten = 0;
    int messages = 0;
    long started = System.currentTimeMillis();
    FileOutputStream output = new FileOutputStream(new File(root, "WAL"));
    try {
      while (bytesWritten < bytesToWrite) {
        ByteBuf buf = Unpooled.buffer();
        while (buf.readableBytes() < cutoff) {
          WriteAheadMessage.Patch patch = new WriteAheadMessage.Patch();
          patch.key = "KeyBasdfgh;ujasdfglkjhasdg";
          patch.space = "spaceasdg;jhksadghasdghjkl";
          patch.changes = new WriteAheadMessage.Change[1];
          patch.changes[0] = new WriteAheadMessage.Change();
          patch.changes[0].request = "asdzxcvuioyasdfhafdga;lk'jmnk;lasfdga'skldgja;sldkgjaskl;jdg;klasg";
          patch.changes[0].redo = "khfgkhgffkghfgjghfjhgfjghfgjhfgjhfjghfjghfjghfjhgfgjhfjhgfjghfjhhgjfgjh";
          patch.changes[0].undo = "asdghasdgasdlkjghasdgfasdghlkajsdglhkjasdglaksjdhglkjasdhgjlkhasdglkjasd";
          messages++;
          WriteAheadMessageCodec.write(buf, patch);
        }
        bytesWritten += buf.readableBytes();
        byte[] buffer = new byte[buf.readableBytes()];
        buf.readBytes(buffer);
        output.write(buffer);
        output.flush();
      }
    } finally {
      output.close();
    }
    Sample sample = new Sample();
    sample.latency = (int) (System.currentTimeMillis() - started); // MS
    sample.bytesWritten = bytesWritten;
    sample.messages = messages;
    return sample;
  }
  public static void raw(File root) throws Exception {
    long mb = 8 * 1024 * 1024;
    System.out.println("cutoff,ms,MB/sec,qps");
    for (int cutoff : new int[] { 64, 128, 196, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152}) {
      ArrayList<Integer> latency = new ArrayList<>();
      Sample warmup = sample(root, cutoff, mb);
      for (int k = 0; k < 40 * 2; k++) {
        latency.add(sample(root, cutoff, mb).latency);
      }
      latency.sort(Integer::compare);
      int ms = latency.get(38 * 2);
      long rate = (long)((1000.0/1024.0 * warmup.bytesWritten / (1024.0 * ms)));
      int qps = (int) (warmup.messages * 1000.0 / ms);
      System.out.println(cutoff + "," + ms + "," + rate + "," + qps);
    }
  }

  private static void logger(File root) throws Exception {

    SimpleExecutor executor = SimpleExecutor.create("wal");
    DiskWriteAheadLog log1 = new DiskWriteAheadLog(executor, new File("WAL2a"), 8192 * 2, 5);
    DiskWriteAheadLog log2 = new DiskWriteAheadLog(executor, new File("WAL2b"), 8192 * 2, 5);
    long started = System.currentTimeMillis();
    int N = 20000;
    CountDownLatch latch = new CountDownLatch(N);
    for (int k = 0; k < N; k++) {
      WriteAheadMessage.Patch patch = new WriteAheadMessage.Patch();
      patch.key = "KeyBasdfgh;ujasdfglkjhasdg";
      patch.space = "spaceasdg;jhksadghasdghjkl";
      patch.changes = new WriteAheadMessage.Change[1];
      patch.changes[0] = new WriteAheadMessage.Change();
      patch.changes[0].request = "asdzxcvuioyasdfhafdga;lk'jmnk;lasfdga'skldgja;sldkgjaskl;jdg;klasg";
      patch.changes[0].redo = "khfgkhgffkghfgjghfjhgfjghfgjhfgjhfjghfjghfjghfjhgfgjhfjhgfjghfjhhgjfgjh" + patch.changes[0].request + patch.changes[0].request;
      patch.changes[0].undo = "asdghasdgasdlkjghasdgfasdghlkajsdglhkjasdglaksjdhglkjasdhgjlkhasdglkjasd" + patch.changes[0].request + patch.changes[0].request + patch.changes[0].undo + patch.changes[0].undo + patch.changes[0].undo;
      (k % 2 == 0 ? log1 : log2).write(patch, new Callback<Void>() {
        @Override
        public void success(Void value) {
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
          latch.countDown();
        }
      });
    }
    latch.await(10000, TimeUnit.MILLISECONDS);
    long ms = System.currentTimeMillis() - started;
    long qps = N * 1000 / ms;
    System.err.println(ms + "," + qps + "," + (int) ((log1.getBytesWritten() + log2.getBytesWritten()) * 1000 / (1024 * 1024.0 * ms)));
  }

  public static void main(String[] args) throws Exception {
    File root = new File("./data-disk/demo-workspace");
    root.mkdir();
    for (int k = 0; k < 100; k++) {
      logger(root);
    }
  }
}
