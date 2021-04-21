/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.operations;

import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.runtime.exceptions.ErrorCodeException;

/** metrics associated with a request */
public class RequestMetrics {
  public final Counter initiate;
  public final Counter success;
  public final Counter results;
  public final Counter failure;
  public final LatencyDistribution latency;
  public final Total inflight;

  public RequestMetrics(CounterFactory factory, String prefix) {
    this.initiate = factory.makeCounter(prefix + "_initiate");
    this.success = factory.makeCounter(prefix + "_success");
    this.results = factory.makeCounter(prefix + "_data");
    this.failure = factory.makeCounter(prefix + "_failure");
    this.latency = factory.makeLatencyDistribution(prefix + "_latency");
    this.inflight = factory.makeTotalTracker(prefix + "_inflight");
  }

  public static CommandResponder wrap(CommandResponder responder, RequestMetrics metrics) {
    metrics.initiate.bump();
    metrics.inflight.inc();
    long started = System.currentTimeMillis();
    return new CommandResponder() {
      boolean finished = false;
      @Override
      public void stream(String json) {
        metrics.results.bump();
        responder.stream(json);
      }

      @Override
      public void finish(String json) {
        done(true);
        responder.finish(json);
      }

      @Override
      public void error(ErrorCodeException ex) {
        metrics.failure.bump();
        done(false);
        responder.error(ex);
      }

      private void done(boolean success) {
        if (!finished) {
          metrics.inflight.dec();
          if (success) {
            metrics.success.bump();
          } else {
            metrics.failure.bump();
          }
          metrics.latency.register((int) (System.currentTimeMillis() - started));
          finished = true;
        }
      }
    };
  }
}
