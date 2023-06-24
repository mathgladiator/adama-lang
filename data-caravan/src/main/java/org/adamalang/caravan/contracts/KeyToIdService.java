/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.contracts;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;

/** convert a key into a long id */
public interface KeyToIdService {
  /** We do this to compact the key into an id that is global */
  void translate(Key key, Callback<Long> callback);

  /** forget about the key */
  void forget(Key key);
}
