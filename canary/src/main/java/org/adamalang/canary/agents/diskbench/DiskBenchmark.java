package org.adamalang.canary.agents.diskbench;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.disk.wal.WriteAheadMessage;
import org.adamalang.disk.wal.WriteAheadMessageCodec;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class DiskBenchmark {
  public static class Sample {
    public int latency;
    public long bytesWritten;
    public int messages;
  }

  private static Sample sample(File root, int cutoff, long bytesToWrite) throws Exception {
    long bytesWritten = 0;
    int messages = 0;
    long started = System.currentTimeMillis();
    File fileToUse = new File(root, "WAL");
    FileOutputStream output = new FileOutputStream(fileToUse);
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
      fileToUse.delete();
    }
    Sample sample = new Sample();
    sample.latency = (int) (System.currentTimeMillis() - started); // MS
    sample.bytesWritten = bytesWritten;
    sample.messages = messages;
    return sample;
  }

  public static void go() throws Exception {
    File root = new File(".");
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
}
