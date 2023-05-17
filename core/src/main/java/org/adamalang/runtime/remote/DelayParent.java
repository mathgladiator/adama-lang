/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.remote;

import org.adamalang.runtime.contracts.RxParent;

/** allows delaying a dirty signal to an event */
public class DelayParent implements RxParent {
  private boolean dirty;
  private Runnable runnable;

  public DelayParent() {
    this.dirty = false;
    this.runnable = null;
  }

  @Override
  public void __raiseDirty() {
    if (this.runnable != null) {
      this.runnable.run();
    } else {
      this.dirty = true;
    }
  }

  @Override
  public boolean __isAlive() {
    return true;
  }

  public void bind(Runnable runnable) {
    this.runnable = runnable;
    if (dirty) {
      this.runnable.run();
      this.dirty = false;
    }
  }
}
