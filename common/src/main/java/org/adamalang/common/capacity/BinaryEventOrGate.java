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

/** Two events are deduped into one */
public abstract class BinaryEventOrGate {
  private boolean a;
  private boolean b;
  private boolean result;
  public BinaryEventOrGate() {
    this.a = false;
    this.b = false;
    this.result = false;
  }

  public void a(boolean value) {
    this.a = value;
    update();
  }

  public void b(boolean value) {
    this.b = value;
    update();
  }

  private void update() {
    boolean next = a || b;
    if (result != next) {
      this.result = next;
      if (this.result) {
        start();
      } else {
        stop();
      }
    }
  }

  public abstract void start();
  public abstract void stop();
}
