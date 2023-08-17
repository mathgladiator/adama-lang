/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.capacity;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.capacity.BinaryEventOrGate;
import org.adamalang.common.capacity.LoadEvent;
import org.adamalang.common.capacity.LoadMonitor;
import org.adamalang.common.capacity.RepeatingSignal;
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
  private final CapacityOverseer overseer;
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

  public CapacityAgent(CapacityMetrics metrics, CapacityOverseer overseer, CoreService service, DeploymentFactoryBase deploymentFactoryBase, ServiceHeatEstimator estimator, SimpleExecutor executor, AtomicBoolean alive, ServiceShield shield, String region, String machine) {
    this.metrics = metrics;
    this.overseer = overseer;
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
          addCapacity();
        }
      });
    }));
    this.rebalance = new BinaryEventOrGate(new RepeatingSignal(executor, alive, 240000, (b) -> {
      executor.execute(new NamedRunnable("capacity-add-rebalance") {
        @Override
        public void execute() throws Exception {
          rebalance();
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

  public void offloadLowSpacesWhileInExecutor() {
    overseer.listAllOnMachine(region, machine, new Callback<List<CapacityInstance>>() {
      @Override
      public void success(List<CapacityInstance> instances) {
        for (CapacityInstance instance : instances) {
          if (instance.override) {
            continue;
          }
          ServiceHeatEstimator.Heat heat = estimator.of(instance.space);
          if (heat.empty) {
            deploymentFactoryBase.undeploy(instance.space);
            overseer.remove(instance.space, region, machine, Callback.DONT_CARE_VOID);
            return;
          }
          if (heat.low) {
            // Don't remove capacity from the prime host
            overseer.pickStableHostForSpace(instance.space, region, new Callback<String>() {
              @Override
              public void success(String stableHost) {
                if (!machine.equals(stableHost)) {
                  LOG.error("shed-traffic:" + instance.space);
                  service.shed((key) -> key.space.equals(instance.space));
                }
              }

              @Override
              public void failure(ErrorCodeException ex) {

              }
            });
          }
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        LOG.error("failed-offload-low-spaces-while-in-executor", ex);
      }
    });
  }

  public void addCapacity() {
    overseer.listAllOnMachine(region, machine, new Callback<List<CapacityInstance>>() {
      @Override
      public void success(List<CapacityInstance> instances) {
        for (CapacityInstance instance : instances) {
          ServiceHeatEstimator.Heat heat = estimator.of(instance.space);
          if (heat.hot) {
            String newHost = null; // TODO: find a new candidate host
            if (newHost != null) {
              overseer.add(instance.space, region, newHost, Callback.DONT_CARE_VOID);
            }
          }
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        LOG.error("failed-rebalance-capacity", ex);
      }
    });
  }

  public void rebalance() {
    overseer.listAllOnMachine(region, machine, new Callback<List<CapacityInstance>>() {
      @Override
      public void success(List<CapacityInstance> instances) {
        for (CapacityInstance instance : instances) {
          ServiceHeatEstimator.Heat heat = estimator.of(instance.space);
          if (heat.hot) {
            overseer.listWithinRegion(instance.space, instance.region, new Callback<List<CapacityInstance>>() {
              @Override
              public void success(List<CapacityInstance> other) {
                          /*
          service.shed((key) -> {
            // TODO: rendevous hash the key to the other, and shed ones that don't fit
            // if the key doesn't win a rendevouz hash, then return true
            return false;
          });
          */
              }

              @Override
              public void failure(ErrorCodeException ex) {
                LOG.error("failed-rebalance-capacity-listing-region", ex);
              }
            });
          }
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        LOG.error("failed-rebalance-capacity", ex);
      }
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
