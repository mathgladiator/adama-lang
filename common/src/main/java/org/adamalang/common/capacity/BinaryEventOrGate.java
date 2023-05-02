/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.capacity;

/** Two events are deduped into one */
public class BinaryEventOrGate {
  private final BoolConsumer event;
  private boolean a;
  private boolean b;
  private boolean result;

  public BinaryEventOrGate(final BoolConsumer event) {
    this.event = event;
    this.a = false;
    this.b = false;
    this.result = false;
  }

  public void a(Boolean value) {
    this.a = value;
    update();
  }

  public void b(Boolean value) {
    this.b = value;
    update();
  }

  private void update() {
    boolean next = a || b;
    if (result != next) {
      this.result = next;
      event.accept(this.result);
    }
  }
}
