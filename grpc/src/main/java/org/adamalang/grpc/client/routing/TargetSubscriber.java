/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client.routing;

import java.util.function.Consumer;

/**
 * wraps a callback from the interested party and then dedupes events such that only distinct
 * elements make it through
 */
public class TargetSubscriber {
  private final Consumer<String> callback;
  private String priorTarget;

  public TargetSubscriber(Consumer<String> callback, String target) {
    this.callback = callback;
    this.priorTarget = target;
    callback.accept(target);
  }

  public void set(String newTarget) {
    if (newTarget != null) {
      if (!newTarget.equals(priorTarget)) {
        priorTarget = newTarget;
        callback.accept(newTarget);
      }
    } else if (priorTarget != null) {
      priorTarget = newTarget;
      callback.accept(null);
    }
  }
}
