/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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

  private void update() {
    boolean next = a || b;
    if (result != next) {
      this.result = next;
      event.accept(this.result);
    }
  }

  public void b(Boolean value) {
    this.b = value;
    update();
  }
}
