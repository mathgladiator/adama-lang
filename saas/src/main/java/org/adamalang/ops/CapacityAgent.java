/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.ops;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.capacity.BinaryEventOrGate;
import org.adamalang.common.capacity.LoadEvent;
import org.adamalang.common.capacity.LoadMonitor;
import org.adamalang.common.capacity.RepeatingSignal;
import org.adamalang.mysql.DataBase;
import org.adamalang.net.client.contracts.HeatMonitor;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.ServiceShield;
import org.adamalang.runtime.sys.metering.MeterReading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/** Sketch of the capacity agent */
public class CapacityAgent implements HeatMonitor  {
  private final CapacityMetrics metrics;
  private final DataBase database;
  private final CoreService service;
  private final SimpleExecutor executor;
  private final LoadMonitor resources;

  public final BinaryEventOrGate add_capacity;
  public final BinaryEventOrGate rebalance;
  public final BinaryEventOrGate rejectNew;
  public final BinaryEventOrGate rejectExisting;
  public final BinaryEventOrGate rejectMessages;

  public CapacityAgent(CapacityMetrics metrics, DataBase database, CoreService service, SimpleExecutor executor, AtomicBoolean alive, ServiceShield shield) {
    this.metrics = metrics;
    this.database = database;
    this.service = service;
    this.executor = executor;
    this.resources = new LoadMonitor(executor, alive);

    this.add_capacity = new BinaryEventOrGate(new RepeatingSignal(executor, alive, 120000, (b) -> {
      executor.execute(new NamedRunnable("capacity-add-capacity") {
        @Override
        public void execute() throws Exception {
          addCapacityWhileInExecutor();
        }
      });
    }));
    this.rebalance = new BinaryEventOrGate(new RepeatingSignal(executor, alive, 240000, (b) -> {
      executor.execute(new NamedRunnable("capacity-add-rebalance") {
        @Override
        public void execute() throws Exception {
          rebalanceWhileInExecutor();
        }
      });
    }));
    this.rejectNew = new BinaryEventOrGate((b) -> {
      metrics.shield_active_new_documents.set(b ? 1 : 0);
      shield.canConnectNew.set(b);
    });
    this.rejectExisting = new BinaryEventOrGate((b) -> {
      metrics.shield_active_existing_connections.set(b ? 1 : 0);
      shield.canConnectExisting.set(b);
    });
    this.rejectMessages = new BinaryEventOrGate((b) -> {
      metrics.shield_active_messages.set(b ? 1 : 0);
      shield.canSendMessageExisting.set(b);
    });

    {
      resources.cpu(new LoadEvent(0.65, add_capacity::a));
      resources.cpu(new LoadEvent(0.75, rebalance::a));
      resources.cpu(new LoadEvent(0.80, rejectNew::a));
      resources.cpu(new LoadEvent(0.85, rejectExisting::a));
      resources.cpu(new LoadEvent(0.90, rejectMessages::a));
    }
    {
      resources.memory(new LoadEvent(0.75, add_capacity::b));
      resources.memory(new LoadEvent(0.80, rebalance::b));
      resources.memory(new LoadEvent(0.85, rejectNew::b));
      resources.memory(new LoadEvent(0.87, rejectExisting::b));
      resources.memory(new LoadEvent(0.90, rejectMessages::b));
    }
  }

  private void addCapacityWhileInExecutor() {
    // TODO:
    // sort the metering records document count, then add capacity to the spaces that make up more than 50% of the host
  }

  private void rebalanceWhileInExecutor() {
    // construct Map Space --> List<Hosts>
    //    for each space on this host (from metering)
    //       pull the capacity per space, intersect with the live hosts (from gossip)
    service.shed((key) -> {
      // if the key doesn't win a rendevouz hash, then return true
      return false;
    });
  }

  public void deliverMeteringRecords(ArrayList<MeterReading> bills) {
    metrics.shield_count_metering.set(bills.size());
    executor.execute(new NamedRunnable("capacity-agent-on-bills") {
      @Override
      public void execute() throws Exception {

      }
    });
  }

  public void deliverAdamaHosts(Collection<String> instances) {
    metrics.shield_count_hosts.set(instances.size());
    executor.execute(new NamedRunnable("capacity-agent-on-instances") {
      @Override
      public void execute() throws Exception {

      }
    });
  }

  @Override
  public void heat(String target, double cpu, double memory) {
    metrics.shield_heat.run();
  }
}
