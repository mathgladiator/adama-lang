package org.adamalang.runtime.sys;

import org.adamalang.runtime.sys.metering.MeterReading;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/** estimate the heat of a particular space from billing records */
public class ServiceHeatEstimator implements Function<ArrayList<MeterReading>, Boolean> {
  public static class Heat {
    public final boolean empty;
    public final boolean low;
    public final boolean hot;

    public Heat(boolean empty, boolean low, boolean hot) {
      this.empty = empty;
      this.low = low;
      this.hot = hot;
    }
  }

  public static class HeatVector {
    public final long cpu;
    public final long messages; // proxy for network
    public final long mem;
    public final long connections;

    public HeatVector(long cpu, long messages, long mem, long connections) {
      this.cpu = cpu;
      this.messages = messages;
      this.mem = mem;
      this.connections = connections;
    }

    public boolean test(HeatVector heat) {
      if (heat.cpu >= cpu) {
        return true;
      }
      if (heat.messages >= messages) {
        return true;
      }
      if (heat.mem >= mem) {
        return true;
      }
      if (heat.connections >= connections) {
        return true;
      }
      return false;
    }

    public static HeatVector add(HeatVector a, HeatVector b) {
      return new HeatVector(a.cpu + b.cpu, a.messages + b.messages, a.mem + b.mem, a.connections + b.connections);
    }
  }

  private final ConcurrentHashMap<String, Heat> heat;
  private final HeatVector low;
  private final HeatVector hot;

  public ServiceHeatEstimator(HeatVector low, HeatVector hot) {
    this.heat = new ConcurrentHashMap<>();
    this.low = low;
    this.hot = hot;
  }

  public Heat of(String space) {
    return heat.get(space);
  }

  @Override
  public Boolean apply(ArrayList<MeterReading> meterReadings) {
    HashMap<String, HeatVector> sums = new HashMap<>();
    for (MeterReading reading : meterReadings) {
      HeatVector current = new HeatVector(reading.cpu, reading.messages, reading.memory, reading.connections);
      HeatVector prior = sums.get(reading.space);
      if (prior == null) {
        sums.put(reading.space, current);
      } else {
        sums.put(reading.space, HeatVector.add(prior, current));
      }
    }
    for (Map.Entry<String, HeatVector> entry : sums.entrySet()) {
      boolean empty = entry.getValue().connections == 0;
      heat.put(entry.getKey(), new Heat(empty, !low.test(entry.getValue()), hot.test(entry.getValue())));
    }
    Iterator<Map.Entry<String, Heat>> it = heat.entrySet().iterator();
    while (it.hasNext()) {
      if (!sums.containsKey(it.next().getKey())) {
        it.remove();
      }
    }
    return true;
  }
}
