/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.canary.agents.simple;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientConnection;
import org.adamalang.web.contracts.WebLifecycle;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleWebSocketConnectionAgent implements WebLifecycle {
  private final SimpleExecutor executor;
  private final SimpleCanaryConfig config;
  private final WebClientBase base;
  private SimpleDocumentConnectionAgent[] agents;
  private AtomicBoolean alive;
  private AtomicInteger connectionsLeft;

  public SimpleWebSocketConnectionAgent(SimpleExecutor executor, SimpleCanaryConfig config, WebClientBase base) {
    this.executor = executor;
    this.config = config;
    this.base = base;
    this.agents = null;
    this.alive = new AtomicBoolean();
    this.connectionsLeft = new AtomicInteger((int) (config.documentsPerConnectionMin + (config.documentsPerConnectionMax - config.documentsPerConnectionMin + 1) * Math.random()));
  }

  public void reportDeath() {
    if (connectionsLeft.decrementAndGet() <= 0) {
      config.quitter.countDown();
    }
  }

  public void kickOff() {
    executor.execute(new NamedRunnable("canary-kickoff") {
      @Override
      public void execute() throws Exception {
        base.open(config.endpoint, SimpleWebSocketConnectionAgent.this);
      }
    });
  }

  @Override
  public void connected(WebClientConnection connection) {
    executor.execute(new NamedRunnable("setting-up-document-agents") {
      @Override
      public void execute() throws Exception {
        SimpleWebSocketConnectionAgent.this.agents = new SimpleDocumentConnectionAgent[connectionsLeft.get()];
        for (int k = 0; k < agents.length; k++) {
          SimpleWebSocketConnectionAgent.this.agents[k] = new SimpleDocumentConnectionAgent(SimpleWebSocketConnectionAgent.this, connection, config);
        }
        for (int k = 0; k < agents.length; k++) {
          SimpleWebSocketConnectionAgent.this.agents[k].kickOff();
        }
      }
    });
  }

  @Override
  public void ping(int latency) {
  }

  @Override
  public void failure(Throwable t) {
    disconnected();
  }

  @Override
  public void disconnected() {
    if (alive.compareAndSet(true, false)) {
      config.quitter.countDown();
    }
  }
}
