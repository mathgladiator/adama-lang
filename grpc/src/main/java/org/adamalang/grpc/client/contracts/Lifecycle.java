/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client.contracts;

import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.proto.InventoryRecord;

import java.util.Collection;

/** a persistent connection is either connected or not */
public interface Lifecycle {
  /** the given client is connected and should be good to use */
  public void connected(InstanceClient client);

  /** the server sent an exceptionally useful heartbeat back */
  public void heartbeat(Collection<InventoryRecord> records);

  /** the given client is disconnected and not good to use */
  public void disconnected(InstanceClient client);
}
