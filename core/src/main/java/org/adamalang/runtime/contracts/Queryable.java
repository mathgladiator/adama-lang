/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.contracts;

import org.adamalang.common.Callback;

import java.util.TreeMap;

/** provides a generic way of querying the infrastructure */
public interface Queryable {

  /** the result is assumed to primed within an object already */
  public void query(TreeMap<String, String> query, Callback<String> callback);
}
