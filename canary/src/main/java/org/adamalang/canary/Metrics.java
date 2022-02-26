package org.adamalang.canary;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Metrics {
  public final AtomicInteger deltas;
  public final AtomicInteger messages_sent;
  public final AtomicInteger messages_acked;
  public final AtomicInteger messages_failed;
  private final ArrayList<Integer> connect_latency;
  private final ArrayList<Integer> send_latency;

  public Metrics() {
    this.deltas = new AtomicInteger(0);
    this.messages_sent = new AtomicInteger(0);
    this.messages_acked = new AtomicInteger(0);
    this.messages_failed = new AtomicInteger(0);
    this.connect_latency = new ArrayList<>();
    this.send_latency = new ArrayList<>();
  }

  public synchronized void record_connect_latency(int x) {
    connect_latency.add(x);
  }

  public synchronized void record_send_latency(int x) {
    send_latency.add(x);
  }

  public synchronized void snapshot() {
    System.out.println(deltas.get() + "," + messages_sent.get() + "," + messages_acked.get() + "," + messages_failed.get()); // TODO: latency
  }
}
