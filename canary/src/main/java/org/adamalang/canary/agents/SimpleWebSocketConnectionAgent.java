/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.canary.agents;

import org.adamalang.canary.CanaryConfig;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientConnection;
import org.adamalang.web.contracts.WebLifecycle;

public class SimpleWebSocketConnectionAgent implements WebLifecycle {
  private final SimpleExecutor executor;
  private final CanaryConfig config;
  private final WebClientBase base;
  private SimpleDocumentConnectionAgent[] agents;

  public SimpleWebSocketConnectionAgent(SimpleExecutor executor, CanaryConfig config, WebClientBase base) {
    this.executor = executor;
    this.config = config;
    this.base = base;
    this.agents = null;
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
        SimpleWebSocketConnectionAgent.this.agents = new SimpleDocumentConnectionAgent[(int) (config.documentsPerConnectionMinimum + (config.documentsPerConnectionMaxmimum - config.documentsPerConnectionMaxmimum) * Math.random())];
        for (int k = 0; k < agents.length; k++) {
          SimpleWebSocketConnectionAgent.this.agents[k] = new SimpleDocumentConnectionAgent(connection, config);
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
    executor.execute(
        new NamedRunnable("disconnected-websocket-agent") {
          @Override
          public void execute() throws Exception {
            if (agents != null) {
              for (int j = 0; j < agents.length; j++) {
                agents[j].kill();
              }
              agents = null;
            }
            executor.schedule(
                new NamedRunnable("retry") {
                  @Override
                  public void execute() throws Exception {
                    base.open(config.endpoint, SimpleWebSocketConnectionAgent.this);
                  }
                },
                2500);
          }
        });
  }
}
