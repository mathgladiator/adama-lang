/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.remote;

import org.adamalang.runtime.contracts.RxChild;

public class RxInvalidate implements RxChild {
  private boolean invalidated;

  public RxInvalidate() {
    this.invalidated = false;
  }

  public boolean getAndClearInvalidated() {
    boolean prior = invalidated;
    invalidated = false;
    return prior;
  }

  @Override
  public boolean __raiseInvalid() {
    this.invalidated = true;
    return false;
  }
}
