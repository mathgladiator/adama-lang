/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys;

/**
 * Used to predict inventory by estimating growth of resources and correcting with a periodic
 * precise inventory
 */
public class PredictiveInventory {
  private final Snapshot[] snapshots;
  private long memory;
  private long ticks;
  private long memory_growth;
  private long ticks_growth;
  private long messages;
  private long count;
  private long connections;

  public PredictiveInventory() {
    this.memory = 0;
    this.ticks = 0;
    this.memory_growth = 0;
    this.ticks_growth = 0;
    this.messages = 0;
    this.count = 0;
    this.snapshots = new Snapshot[4];
    this.connections = 0;
  }

  public MeteringSample sample() {
    MeteringSample meteringSample = new MeteringSample(memory, ticks, count, messages, connections);
    messages = 0;
    return meteringSample;
  }

  /** provide a precise and accurate accounting of the state */
  public void accurate(PreciseSnapshotAccumulator preciseSnapshotAccumulator) {
    // we put this in the buffer
    for (int k = 0; k < snapshots.length - 1; k++) {
      snapshots[k] = snapshots[k + 1];
    }
    snapshots[snapshots.length - 1] =
        new Snapshot(
            preciseSnapshotAccumulator.memory,
            preciseSnapshotAccumulator.ticks,
            preciseSnapshotAccumulator.count,
            preciseSnapshotAccumulator.connections);

    // absorb the precision
    this.memory = preciseSnapshotAccumulator.memory;
    this.ticks = preciseSnapshotAccumulator.ticks;
    this.count = preciseSnapshotAccumulator.count;
    this.connections = preciseSnapshotAccumulator.connections;

    // compute the average document size and use that as the estimate growth
    this.memory_growth = 0;
    this.ticks_growth = 0;
    long n = 0;
    for (int k = 0; k < snapshots.length; k++) {
      if (snapshots[k] != null) {
        this.memory_growth += snapshots[k].memory;
        this.ticks_growth += snapshots[k].ticks;
        n += snapshots[k].count;
      }
    }
    if (n > 0) {
      this.memory_growth /= n;
      this.ticks_growth /= n;
    }
  }

  public void grow() {
    this.memory += memory_growth;
    this.ticks += ticks_growth;
    this.count++;
  }

  public void message() {
    this.messages++;
  }

  public void connect() {
    this.connections++;
  }

  public static class MeteringSample {
    public final long memory;
    public final long cpu;
    public final long count;
    public final long messages;
    public final long connections;

    public MeteringSample(long memory, long cpu, long count, long messages, long connections) {
      this.memory = memory;
      this.cpu = cpu;
      this.count = count;
      this.messages = messages;
      this.connections = connections;
    }

    public static MeteringSample add(MeteringSample a, MeteringSample b) {
      return new MeteringSample(
          a.memory + b.memory,
          a.cpu + b.cpu,
          a.count + b.count,
          a.messages + b.messages,
          a.connections + b.connections);
    }
  }

  public static class PreciseSnapshotAccumulator {
    public long memory;
    public long ticks;
    public int count;
    public long connections;

    public PreciseSnapshotAccumulator() {
      this.memory = 0;
      this.ticks = 0;
      this.count = 0;
      this.connections = 0;
    }
  }

  private class Snapshot {
    private final long memory;
    private final long ticks;
    private final long count;
    private final long connections;

    public Snapshot(long memory, long ticks, long count, long connections) {
      this.memory = memory;
      this.ticks = ticks;
      this.count = count;
      this.connections = connections;
    }
  }
}
