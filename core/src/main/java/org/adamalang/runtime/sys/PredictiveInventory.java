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
package org.adamalang.runtime.sys;

/**
 * Used to predict inventory by estimating growth of resources and correcting with a periodic
 * precise inventory
 */
public class PredictiveInventory {
  private final Snapshot[] snapshots;
  private long memory; // estimate-standing;
  private long memory_growth;
  private long ticks; // estimate-standing
  private long ticks_growth;
  private long messages; // reset
  private long count; // estimate-standing
  private long connections; // estimate-standing
  private long connections_growth;
  private long bandwidth; // reset
  private long first_party_service_calls; // reset
  private long third_party_service_calls; // reset
  private long cpu_milliseconds; // reset
  private long backup_byte_hours; // reset

  public PredictiveInventory() {
    this.memory = 0;
    this.ticks = 0;
    this.memory_growth = 0;
    this.ticks_growth = 0;
    this.messages = 0;
    this.count = 0;
    this.snapshots = new Snapshot[4];
    this.connections = 0;
    this.bandwidth = 0;
    this.first_party_service_calls = 0;
    this.third_party_service_calls = 0;
    this.cpu_milliseconds = 0;
    this.backup_byte_hours = 0;
  }

  public MeteringSample sample() {
    MeteringSample meteringSample = new MeteringSample(memory, ticks, count, messages, connections, bandwidth, first_party_service_calls, third_party_service_calls, cpu_milliseconds, backup_byte_hours);
    messages = 0;
    bandwidth = 0;
    first_party_service_calls = 0;
    third_party_service_calls = 0;
    cpu_milliseconds = 0;
    backup_byte_hours = 0;
    return meteringSample;
  }

  /** provide a precise and accurate accounting of the state */
  public void accurate(PreciseSnapshotAccumulator preciseSnapshotAccumulator) {
    // we put this in the buffer
    for (int k = 0; k < snapshots.length - 1; k++) {
      snapshots[k] = snapshots[k + 1];
    }
    snapshots[snapshots.length - 1] = new Snapshot(preciseSnapshotAccumulator.memory, preciseSnapshotAccumulator.ticks, preciseSnapshotAccumulator.count, preciseSnapshotAccumulator.connections);

    // absorb the precision
    this.memory = preciseSnapshotAccumulator.memory;
    this.ticks = preciseSnapshotAccumulator.ticks;
    this.count = preciseSnapshotAccumulator.count;
    this.connections = preciseSnapshotAccumulator.connections;
    this.cpu_milliseconds = preciseSnapshotAccumulator.cpu_ms;

    // compute the average document size and use that as the estimate growth
    this.memory_growth = 0;
    this.ticks_growth = 0;
    this.connections_growth = 0;
    long n = 0;
    for (int k = 0; k < snapshots.length; k++) {
      if (snapshots[k] != null) {
        this.memory_growth += snapshots[k].memory;
        this.ticks_growth += snapshots[k].ticks;
        this.connections_growth += snapshots[k].connections;
        n += snapshots[k].count;
      }
    }
    if (n > 0) {
      this.memory_growth /= n;
      this.ticks_growth /= n;
      this.connections_growth /= n;
    }
  }

  public void grow() {
    this.memory += memory_growth;
    this.ticks += ticks_growth;
    this.connections += connections_growth;
    this.count++;
  }

  public void message() {
    this.messages++;
  }

  public void connect() {
    this.connections++;
  }

  public void bandwidth(long bytes) {
    this.bandwidth += bytes;
  }

  public void first_party_service_call() {
    this.first_party_service_calls++;
  }

  public void third_party_service_call() {
    this.third_party_service_calls++;
  }

  public void backup(long byteHours) {
    backup_byte_hours += byteHours;
  }

  public void cpu_ms(long ms) {
    cpu_milliseconds += ms;
  }

  public static class MeteringSample {
    public final long memory;
    public final long cpu;
    public final long count;
    public final long messages;
    public final long connections;
    public final long bandwidth;
    public final long first_party_service_calls;
    public final long third_party_service_calls;
    public final long cpu_milliseconds;
    public final long backup_byte_hours;

    public MeteringSample(long memory, long cpu, long count, long messages, long connections, long bandwidth, long first_party_service_calls, long third_party_service_calls, long cpu_milliseconds, long backup_byte_hours) {
      this.memory = memory;
      this.cpu = cpu;
      this.count = count;
      this.messages = messages;
      this.connections = connections;
      this.bandwidth = bandwidth;
      this.first_party_service_calls = first_party_service_calls;
      this.third_party_service_calls = third_party_service_calls;
      this.cpu_milliseconds = cpu_milliseconds;
      this.backup_byte_hours = backup_byte_hours;
    }

    public static MeteringSample justMemory(long memory) {
      return new MeteringSample(memory, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static MeteringSample add(MeteringSample a, MeteringSample b) {
      return new MeteringSample(a.memory + b.memory, //
          a.cpu + b.cpu, //
          a.count + b.count, //
          a.messages + b.messages, //
          a.connections + b.connections, //
          a.bandwidth + b.bandwidth, //
          a.first_party_service_calls + b.first_party_service_calls, //
          a.third_party_service_calls + b.third_party_service_calls,
          a.cpu_milliseconds + b.cpu_milliseconds,
          a.backup_byte_hours + b.backup_byte_hours);
    }
  }

  public static class PreciseSnapshotAccumulator {
    public long memory;
    public long ticks;
    public int count;
    public long connections;
    public long bandwidth;
    public long cpu_ms;

    public PreciseSnapshotAccumulator() {
      this.memory = 0;
      this.ticks = 0;
      this.count = 0;
      this.connections = 0;
      this.bandwidth = 0;
      this.cpu_ms = 0;
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
