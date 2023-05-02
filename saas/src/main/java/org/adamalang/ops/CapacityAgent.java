/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.ops;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.capacity.BinaryEventOrGate;
import org.adamalang.common.capacity.LoadEvent;
import org.adamalang.common.capacity.LoadMonitor;
import org.adamalang.common.capacity.RepeatingSignal;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.CapacityInstance;
import org.adamalang.mysql.model.Capacity;
import org.adamalang.mysql.model.Hosts;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.contracts.HeatMonitor;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.ServiceHeatEstimator;
import org.adamalang.runtime.sys.ServiceShield;
import org.adamalang.runtime.sys.metering.MeterReading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/** Sketch of the capacity agent */
public class CapacityAgent implements HeatMonitor  {
  private final Logger LOG = LoggerFactory.getLogger(CapacityAgent.class);
  private final CapacityMetrics metrics;
  private final DataBase database;
  private final CoreService service;
  private final DeploymentFactoryBase deploymentFactoryBase;
  private final ServiceHeatEstimator estimator;
  private final SimpleExecutor executor;
  private final LoadMonitor resources;
  private final String region;
  private final String machine;

  public final BinaryEventOrGate add_capacity;
  public final BinaryEventOrGate rebalance;
  public final BinaryEventOrGate rejectNew;
  public final BinaryEventOrGate rejectExisting;
  public final BinaryEventOrGate rejectMessages;

  public CapacityAgent(CapacityMetrics metrics, DataBase database, CoreService service, DeploymentFactoryBase deploymentFactoryBase, ServiceHeatEstimator estimator, SimpleExecutor executor, AtomicBoolean alive, ServiceShield shield, String region, String machine) {
    this.metrics = metrics;
    this.database = database;
    this.service = service;
    this.deploymentFactoryBase = deploymentFactoryBase;
    this.estimator = estimator;
    this.executor = executor;
    this.resources = new LoadMonitor(executor, alive);
    this.region = region;
    this.machine = machine;
    executor.schedule(new NamedRunnable("capacity-offload") {
      @Override
      public void execute() throws Exception {
        if (alive.get()) {
          offloadLowSpacesWhileInExecutor();
          executor.schedule(this, (int) (90000 + 90000 * Math.random()));
        }
      }
    }, 120000); // every two minutes
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
      LOG.error(b ? "rejecting new documents" : "accepting new documents");
      metrics.shield_active_new_documents.set(b ? 1 : 0);
      shield.canConnectNew.set(!b);
    });
    this.rejectExisting = new BinaryEventOrGate((b) -> {
      LOG.error(b ? "rejecting traffic to existing documents" : "allowing traffic to existing documents");
      metrics.shield_active_existing_connections.set(b ? 1 : 0);
      shield.canConnectExisting.set(!b);
    });
    this.rejectMessages = new BinaryEventOrGate((b) -> {
      LOG.error(b ? "rejecting messages" : "allowing messages");
      metrics.shield_active_messages.set(b ? 1 : 0);
      shield.canSendMessageExisting.set(!b);
    });

    {
      resources.cpu(new LoadEvent(0.75, add_capacity::a));
      resources.cpu(new LoadEvent(0.85, rebalance::a));
      resources.cpu(new LoadEvent(0.90, rejectNew::a));
      resources.cpu(new LoadEvent(0.95, rejectExisting::a));
      resources.cpu(new LoadEvent(0.98, rejectMessages::a));
    }
    {
      resources.memory(new LoadEvent(0.80, add_capacity::b));
      resources.memory(new LoadEvent(0.85, rebalance::b));
      resources.memory(new LoadEvent(0.90, rejectNew::b));
      resources.memory(new LoadEvent(0.92, rejectExisting::b));
      resources.memory(new LoadEvent(0.95, rejectMessages::b));
    }
  }

  private void offloadLowSpacesWhileInExecutor() {
    try {
      for (CapacityInstance instance : Capacity.listAllOnMachine(database, region, machine)) {
        if (instance.override) {
          continue;
        }
        ServiceHeatEstimator.Heat heat = estimator.of(instance.space);
        if (heat.empty) {
          deploymentFactoryBase.undeploy(instance.space);
          Capacity.remove(database, instance.space, region, machine);
          return;
        }
        if (heat.low) {
          // Don't remove capacity from the prime host
          String stableHost = Hosts.pickStableHostFromRegion(database, region, "adama", instance.space);
          if (!machine.equals(stableHost)) {
            LOG.error("shed-traffic:" + instance.space);
            service.shed((key) -> key.space.equals(instance.space));
          }
        }
      }
    } catch (Exception ex) {
      LOG.error("failed-offload-low-spaces-while-in-executor");
    }
  }

  private void addCapacityWhileInExecutor() {
    try {
      for (CapacityInstance instance : Capacity.listAllOnMachine(database, region, machine)) {
        ServiceHeatEstimator.Heat heat = estimator.of(instance.space);
        if (heat.hot) {
          String newHost = null; // TODO: find a new candidate host
          if (newHost != null) {
            Capacity.add(database, instance.space, region, newHost);
            // TODO: inform the new host (or the new host will poll the DB)
          }
        }
      }
    } catch (Exception ex) {
      LOG.error("failed-rebalance-capacity", ex);
    }
  }

  private void rebalanceWhileInExecutor() {
    try {
      for (CapacityInstance instance : Capacity.listAllOnMachine(database, region, machine)) {
        ServiceHeatEstimator.Heat heat = estimator.of(instance.space);
        if (heat.hot) {
          List<CapacityInstance> other = Capacity.listRegion(database, instance.space, instance.region);
          /*
          service.shed((key) -> {
            // TODO: rendevous hash the key to the other, and shed ones that don't fit
            // if the key doesn't win a rendevouz hash, then return true
            return false;
          });
          */
        }
      }
    } catch (Exception ex) {
      LOG.error("failed-rebalance-capacity", ex);
    }
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
