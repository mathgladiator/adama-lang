/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.pool;

/** a wrapper around an item which is used to report status on the item */
public interface PoolItem<S> {
  /** @return the item */
  public S item();

  /** signal the item has a failure and should not be re-used again */
  public void signalFailure();

  /** return the item to the pool */
  public void returnToPool();
}
