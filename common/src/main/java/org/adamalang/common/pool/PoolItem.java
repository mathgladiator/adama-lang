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
