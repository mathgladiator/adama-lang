/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.overlord.roles;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.gossip.Engine;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.deployments.Deployments;
import org.adamalang.overlord.OverlordMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.TreeSet;

public class DeploymentReconciliation {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentReconciliation.class);

  public static void kickOff(OverlordMetrics metrics, Engine engine, DataBase deploymentsDatabase) {
    StateMachine sm = new StateMachine(metrics, deploymentsDatabase);
    engine.subscribe("adama", sm::setTargets);
    Runtime.getRuntime().addShutdownHook(new Thread(sm::stop));
  }

  private enum StateLabel {
    Initialized,
    GotTargets,
    Stable,
    Reconciled,
  }

  private static class StateMachine {
    private final OverlordMetrics metrics;
    private final DataBase dataBase;
    private final SimpleExecutor offload;
    private final TreeSet<String> targets;
    public long lastGotTargets;
    private StateLabel label;

    private StateMachine(OverlordMetrics metrics, DataBase dataBase) {
      this.metrics = metrics;
      this.dataBase = dataBase;
      this.offload = SimpleExecutor.create("deployment-reconciliation");
      this.lastGotTargets = -1;
      this.targets = new TreeSet<>();
      this.label = StateLabel.Initialized;
    }

    private void reconcileWhileInExecutor(long lockedAt) {
      if (label != StateLabel.Stable && lastGotTargets == lockedAt) {
        metrics.reconcile_abort_listing.run();
        return;
      }
      try {
        metrics.reconcile_begin_listing.run();
        for (String deployedTarget : Deployments.listAllTargets(dataBase)) {
          metrics.reconcile_consider_target.run();
          // the deployed target no longer exists in the fleet, so kill it with fire
          if (!targets.contains(deployedTarget)) {
            metrics.reconcile_evict_target.run();
            System.err.println("evict:" + deployedTarget);
            LOGGER.info("evicting", deployedTarget);
            Deployments.undeployTarget(dataBase, deployedTarget);
          }
        }
        metrics.reconcile_end_listing.run();
        label = StateLabel.Reconciled;
      } catch (Exception ex) {
        metrics.reconcile_failed_listing.run();
        LOGGER.error("failed-reconciliation", ex);
        offload.schedule(
            new NamedRunnable("try-reconcile-again") {
              @Override
              public void execute() throws Exception {
                reconcileWhileInExecutor(lockedAt);
              }
            },
            2500);
      }
    }

    private void stabilityCheckWhileInExecutor() {
      metrics.reconcile_stability_check_init.run();
      long timeSinceLastGotTargets = System.currentTimeMillis() - lastGotTargets;
      if (timeSinceLastGotTargets > 4000 && label == StateLabel.GotTargets) {
        metrics.reconcile_stability_check_success.run();
        label = StateLabel.Stable;
        reconcileWhileInExecutor(lastGotTargets);
      } else {
        metrics.reconcile_stability_check_failed.run();
      }
    }

    public void setTargets(Collection<String> newTargets) {
      offload.execute(
          new NamedRunnable("got-targets-from-gossip") {
            @Override
            public void execute() throws Exception {
              metrics.reconcile_start.run();
              targets.clear();
              targets.addAll(newTargets);
              StateMachine.this.lastGotTargets = System.currentTimeMillis();
              StateMachine.this.label = StateLabel.GotTargets;
              offload.schedule(
                  new NamedRunnable("stability-check") {
                    @Override
                    public void execute() throws Exception {
                      stabilityCheckWhileInExecutor();
                    }
                  },
                  5000);
            }
          });
    }

    public void stop() {
      offload.shutdown();
    }
  }
}
