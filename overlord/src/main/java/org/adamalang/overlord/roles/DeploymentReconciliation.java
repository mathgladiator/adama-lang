/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.overlord.roles;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.gossip.Engine;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Deployments;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.overlord.html.FixedHtmlStringLoggerTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.TreeSet;

public class DeploymentReconciliation {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentReconciliation.class);

  public static void kickOff(OverlordMetrics metrics, Engine engine, DataBase deploymentsDatabase, ConcurrentCachedHttpHandler handler) {
    StateMachine sm = new StateMachine(metrics, deploymentsDatabase, handler);
    engine.subscribe("adama", sm::setTargets);
    Runtime.getRuntime().addShutdownHook(new Thread(sm::stop));
  }

  private enum StateLabel {
    Initialized, GotTargets, Stable, Reconciled,
  }

  private static class StateMachine {
    private final OverlordMetrics metrics;
    private final DataBase dataBase;
    private final SimpleExecutor offload;
    private final TreeSet<String> targets;
    private final ConcurrentCachedHttpHandler handler;
    private final FixedHtmlStringLoggerTable table;
    public long lastGotTargets;
    private StateLabel label;

    private StateMachine(OverlordMetrics metrics, DataBase dataBase, ConcurrentCachedHttpHandler handler) {
      this.metrics = metrics;
      this.dataBase = dataBase;
      this.offload = SimpleExecutor.create("deployment-reconciliation");
      this.lastGotTargets = -1;
      this.targets = new TreeSet<>();
      this.label = StateLabel.Initialized;
      this.handler = handler;
      this.table = new FixedHtmlStringLoggerTable(128, "action", "info");
      makeReport();
    }

    private void makeReport() {
      handler.put("/reconcile", table.toHtml("Deployment Reconciliation"));
    }

    private void reconcileWhileInExecutor(long lockedAt) {
      if (label != StateLabel.Stable && lastGotTargets == lockedAt) {
        metrics.reconcile_abort_listing.run();
        return;
      }
      try {
        long started = System.currentTimeMillis();
        table.row("reconciliation-started", "" + started);
        metrics.reconcile_begin_listing.run();
        for (String deployedTarget : Deployments.listAllTargets(dataBase)) {
          metrics.reconcile_consider_target.run();
          // the deployed target no longer exists in the fleet, so kill it with fire
          if (!targets.contains(deployedTarget)) {
            metrics.reconcile_evict_target.run();
            table.row("evict", deployedTarget);
            LOGGER.info("evicting", deployedTarget);
            Deployments.undeployTarget(dataBase, deployedTarget);
          }
        }
        metrics.reconcile_end_listing.run();
        label = StateLabel.Reconciled;
        table.row("reconciliation-finished", "ms=" + (System.currentTimeMillis() - started));
      } catch (Exception ex) {
        metrics.reconcile_failed_listing.run();
        table.row("failed-reconciliation", ex.getMessage());
        LOGGER.error("failed-reconciliation", ex);
        offload.schedule(new NamedRunnable("try-reconcile-again") {
          @Override
          public void execute() throws Exception {
            reconcileWhileInExecutor(lockedAt);
          }
        }, 2500);
      } finally {
        makeReport();
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
      offload.execute(new NamedRunnable("got-targets-from-gossip") {
        @Override
        public void execute() throws Exception {
          StringBuilder newTargetsAsString = new StringBuilder();
          boolean append = false;
          for (String target : new TreeSet<>(newTargets)) {
            if (append) {
              newTargetsAsString.append(", ");
            }
            append = true;
            newTargetsAsString.append(target);
          }
          table.row("got-targets", newTargetsAsString.toString());
          makeReport();
          metrics.reconcile_start.run();
          targets.clear();
          targets.addAll(newTargets);
          StateMachine.this.lastGotTargets = System.currentTimeMillis();
          StateMachine.this.label = StateLabel.GotTargets;
          offload.schedule(new NamedRunnable("stability-check") {
            @Override
            public void execute() throws Exception {
              stabilityCheckWhileInExecutor();
            }
          }, 5000);
        }
      });
    }

    public void stop() {
      offload.shutdown();
    }
  }
}
