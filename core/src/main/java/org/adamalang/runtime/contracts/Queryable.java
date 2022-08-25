package org.adamalang.runtime.contracts;

import org.adamalang.common.Callback;

import java.util.TreeMap;

/** provides a generic way of querying the infrastructure */
public interface Queryable {

  /** the result is assumed to primed within an object already */
  public void query(TreeMap<String, String> query, Callback<String> callback);
}
