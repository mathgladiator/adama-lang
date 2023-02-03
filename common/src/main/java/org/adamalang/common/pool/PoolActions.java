package org.adamalang.common.pool;

import org.adamalang.common.Callback;

/** The pool is asynchronous and has to make external calls to create and kill items within the pool */
public interface PoolActions<R, S> {
  /** request an item to be created to be placed within the pool */
  public void create(R request, Callback<S> created);

  /** destroy an item as it is leaving the pool */
  public void destroy(S item);
}
