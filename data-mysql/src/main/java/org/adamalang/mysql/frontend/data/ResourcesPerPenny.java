/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.frontend.data;

public class ResourcesPerPenny {
  public double cpu;
  public double messages;
  public double count;
  public double memory;
  public double connections;
  public long storage;

  public ResourcesPerPenny(double cpu, double messages, double count, double memory, double connections, long storage) {
    this.cpu = cpu;
    this.messages = messages;
    this.count = count;
    this.memory = memory;
    this.connections = connections;
    this.storage = storage;
  }
}
