/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.data;

import org.adamalang.common.ErrorCodeException;

/** Observe data changes at a lower level */
public interface DataObserver {
  /** an initial payload representing a snapshot */
  public void start(String snapshot);

  /** a rollback happened */
  public void change(String delta);

  /** a failure occurred */
  public void failure(ErrorCodeException exception);
}
