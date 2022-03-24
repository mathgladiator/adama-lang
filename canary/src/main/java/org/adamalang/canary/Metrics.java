/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.canary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Metrics {
  public final AtomicInteger deltas;
  public final AtomicInteger messages_sent;
  public final AtomicInteger messages_acked;
  public final AtomicInteger messages_failed;
  public final AtomicInteger stream_failed;
  private final ArrayList<Integer> connect_latency;
  private final ArrayList<Integer> send_latency;
  private final HashMap<Integer, Integer> failure_reasons;

  private int prior_deltas;
  private int prior_messages_sent;
  private int prior_messages_acked;
  private int prior_messages_failed;

  public Metrics() {
    this.deltas = new AtomicInteger(0);
    this.messages_sent = new AtomicInteger(0);
    this.messages_acked = new AtomicInteger(0);
    this.messages_failed = new AtomicInteger(0);
    this.stream_failed = new AtomicInteger(0);
    this.prior_deltas = 0;
    this.prior_messages_sent = 0;
    this.prior_messages_acked = 0;
    this.prior_messages_failed = 0;
    this.failure_reasons = new HashMap();
    this.connect_latency = new ArrayList<>();
    this.send_latency = new ArrayList<>();
    System.out.println("| Deltas | Sent | Send Ack | Send Fail | Stream Fail | p5 Latency | p50 Latency | p98 Latency | Errors |");
    System.out.println("| --- | --- | --- | --- | --- | --- | --- | --- | --- |");
  }

  public synchronized void record_connect_latency(int x) {
    connect_latency.add(x);
  }

  public synchronized void record_send_latency(int x) {
    send_latency.add(x);
  }

  public synchronized void report_failure(int code) {
    Integer prior = failure_reasons.get(code);
    if (prior == null) {
      failure_reasons.put(code, 1);
    } else {
      failure_reasons.put(code, prior + 1);
    }
  }

  public synchronized void snapshot() {
    StringBuilder sb = new StringBuilder();
    boolean append = false;
    for (Map.Entry<Integer, Integer> entry : failure_reasons.entrySet()) {
      if (append) {
        sb.append("|");
      }
      append = true;
      sb.append(entry.getKey() + "=" + entry.getValue());
    }
    send_latency.sort(Integer::compare);
    System.out.println("| " + (deltas.get() - prior_deltas) + " | " + (messages_sent.get() - prior_messages_sent) + " | " + (messages_acked.get() - prior_messages_acked) + " | " + (messages_failed.get() - prior_messages_failed) + " | " + stream_failed.get() + " | " + latency(0.05) + " | " + latency(0.50) + " | " + latency(0.98) + " | " + sb + " | ");
    send_latency.clear();
    this.prior_deltas = deltas.get();
    this.prior_messages_sent = messages_sent.get();
    this.prior_messages_acked = messages_acked.get();
    this.prior_messages_failed = messages_failed.get();
  }

  private int latency(double p) {
    if (send_latency.size() > 10) {
      return send_latency.get((int) (send_latency.size() * p));
    } else {
      return -1;
    }
  }
}
