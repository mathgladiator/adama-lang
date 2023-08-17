/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.capacity;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;

/** pick a machine from the current region */
public interface MachinePicker {

  /** pick a host for the given key */
  public void pickHost(Key key, Callback<String> callback);
}
