package org.adamalang.mysql.frontend.data;

public class BillingUsage {
  public final int hour;
  public final long cpu;
  public final long memory;
  public final int connections;
  public final int documents;
  public final int messages;
  public final long storageBytes;

  public BillingUsage(int hour, long cpu, long memory, int connections, int documents, int messages, long storageBytes) {
    this.hour = hour;
    this.cpu = cpu;
    this.memory = memory;
    this.connections = connections;
    this.documents = documents;
    this.messages = messages;
    this.storageBytes = storageBytes;
  }
}
