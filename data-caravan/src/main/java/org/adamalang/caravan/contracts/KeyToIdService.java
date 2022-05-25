/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.caravan.contracts;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;

/** convert a key into a long id */
public interface KeyToIdService {
  /** We do this to compact the key into an id that is global */
  public void translate(Key key, Callback<Long> callback);
}
