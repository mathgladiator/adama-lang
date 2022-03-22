package org.adamalang.bald.play.benchmarks.scenarios;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class Scenario {
  public interface Driver {
    public void append(String key, int seq, byte[] value) throws Exception;

    public void flush() throws Exception;
  }

  public ArrayList<Sample> list;

  public static class Sample {
    public final String key;
    public final int seq;
    public final byte[] value;

    public Sample(String key, int seq, byte[] value) {
      this.key = key;
      this.seq = seq;
      this.value = value;
    }
  }

  public final long bytes;
  public final int writes;
  public Scenario(int keys, int writesPerKey, String[] values) {
    byte[][] valuesBytes = new byte[values.length][];
    for (int k = 0; k < values.length; k++) {
      valuesBytes[k] = values[k].getBytes(StandardCharsets.UTF_8);
    }
    long sumBytes = 0;
    Random rng = new Random();
    list = new ArrayList<>();
    for (int key = 0; key < keys; key++) {
      for (int write = 0; write < writesPerKey; write++) {
        Sample sample = new Sample("space/keyname/" + key, write, valuesBytes[rng.nextInt(values.length)]);
        sumBytes += sample.key.length() + 4 + sample.value.length;
        list.add(sample);
      }
    }
    this.bytes = sumBytes;
    for (int k = 2; k < list.size(); k++) {
      int at = rng.nextInt(k);
      Sample swap = list.get(at);
                    list.set(at, list.get(k));
                                 list.set(k, swap);
    }
    this.writes = list.size();
  }

  public void drive(Driver driver) throws Exception {
    long started = System.currentTimeMillis();
    for (Sample sample : list) {
      driver.append(sample.key, sample.seq, sample.value);
    }
    driver.flush();
    long time = System.currentTimeMillis() - started;
    double MB = bytes / (1024.0 * 1024.0);
    double MBperSecond = (bytes * 1000.0) / (time * 1024 * 1024.0);
    double writesPerSecond = (writes * 1000.0) / time;
    System.out.println("| " + Math.round(MB * 100.0) / 100.0 + " | " + Math.round(MBperSecond * 100) / 100.0 + " | " + time + " | " + Math.round(writesPerSecond) + " |");

  }
}
