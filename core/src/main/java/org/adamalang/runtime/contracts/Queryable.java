/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.contracts;

import org.adamalang.common.Callback;

import java.util.TreeMap;

/** provides a generic way of querying the infrastructure */
public interface Queryable {

  /** the result is assumed to primed within an object already */
  void query(TreeMap<String, String> query, Callback<String> callback);
}
