/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.data;

public class BillingUsage {
  public final int hour;
  public final long cpu;
  public final long memory;
  public final int connections;
  public final int documents;
  public final int messages;
  public final long storageBytes;
  public final long bandwidth;
  public final long firstPartyServiceCalls;
  public final long thirdPartyServiceCalls;

  public BillingUsage(int hour, long cpu, long memory, int connections, int documents, int messages, long storageBytes, long bandwidth, long firstPartyServiceCalls, long thirdPartyServiceCalls) {
    this.hour = hour;
    this.cpu = cpu;
    this.memory = memory;
    this.connections = connections;
    this.documents = documents;
    this.messages = messages;
    this.storageBytes = storageBytes;
    this.bandwidth = bandwidth;
    this.firstPartyServiceCalls = firstPartyServiceCalls;
    this.thirdPartyServiceCalls = thirdPartyServiceCalls;
  }
}
