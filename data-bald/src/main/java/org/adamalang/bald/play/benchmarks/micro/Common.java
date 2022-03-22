package org.adamalang.bald.play.benchmarks.micro;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class Common {

  public byte[][] generateSamples(int repeat) {
    StringBuilder sb0 = new StringBuilder();
    StringBuilder sb1 = new StringBuilder();
    StringBuilder sb2 = new StringBuilder();
    StringBuilder sb3 = new StringBuilder();
    StringBuilder sb4 = new StringBuilder();
    for (int k = 0; k < repeat; k++) {
      sb0.append("ABCD1234ABCD1234");
      sb1.append("ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234");
      sb2.append("ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234");
      sb3.append("ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234");
      sb4.append("ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234ABCD1234");
    }
    byte[] b0 = sb0.toString().getBytes(StandardCharsets.UTF_8);
    byte[] b1 = sb1.toString().getBytes(StandardCharsets.UTF_8);
    byte[] b2 = sb2.toString().getBytes(StandardCharsets.UTF_8);
    byte[] b3 = sb3.toString().getBytes(StandardCharsets.UTF_8);
    byte[] b4 = sb4.toString().getBytes(StandardCharsets.UTF_8);
    return new byte[][] { b0, b0, b0, b0, b1, b1, b1, b2, b2, b3, b4 };
  }

  private static class Row {
    public double mb;
    public double throughput_mb_per_sec;
    public double average_latency;
  }
  private ArrayList<Row> rows;

  private long started;
  private Random rng;
  private long bytes;
  private int samples;

  public Common() {
    this.rows = new ArrayList<>();
    this.rng = new Random();
  }

  public void start() {
    this.started = System.currentTimeMillis();
    this.bytes = 0L;
    this.samples = 0;
  }

  public byte[] pick(byte[][] samples) {
    return samples[rng.nextInt(samples.length)];
  }

  public void report(int size) {
    this.bytes += size;
    this.samples++;
  }

  public void stop() {
    long time = System.currentTimeMillis() - started;
    if (time == 0) {
      time = 1;
    }
    Row row = new Row();
    row.mb = bytes / (1024.0 * 1024.0);
    row.throughput_mb_per_sec = (bytes * 1000.0) / (1024.0 * 1024.0 * time);
    row.average_latency = ((double) time) / samples;;
    rows.add(row);
  }

  public void dump() {
    System.out.println("MB,MB/second,AvgLatencyMS");
    for (Row row : rows) {
      System.out.println(row.mb + "," + row.throughput_mb_per_sec + "," + row.average_latency);
    }
  }
}
