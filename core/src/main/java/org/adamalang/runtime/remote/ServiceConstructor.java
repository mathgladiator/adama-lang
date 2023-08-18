/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.remote;

import org.adamalang.common.keys.PrivateKeyBundle;

import java.util.HashMap;
import java.util.TreeMap;

/** constructor for a service */
public interface ServiceConstructor {

  public Service cons(String space, HashMap<String, Object> params, TreeMap<Integer, PrivateKeyBundle> keys);
}
