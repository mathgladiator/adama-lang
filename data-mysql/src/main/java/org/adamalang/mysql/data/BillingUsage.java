/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
