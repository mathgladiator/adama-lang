/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.capacity;

/** a load event fires when an associated metric is elevated */
public abstract class LoadEvent {
  private final double threshold;
  private boolean active;

  public LoadEvent(double threshold) {
    this.threshold = threshold;
    this.active = false;
  }

  /** provide a sample of some metric */
  public void at(double metric) {
    boolean next = metric > threshold;
    if (active != next) {
      active = next;
      if (next) {
        start();
      } else {
        stop();
      }
    }
  }

  /** The monitored value has breached the threshold */
  public abstract void start();

  /** The monitored value no longer breaches the threshold */
  public abstract void stop();
}
