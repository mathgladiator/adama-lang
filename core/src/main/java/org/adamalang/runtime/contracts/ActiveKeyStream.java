/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.contracts;

import org.adamalang.common.ErrorCodeException;

/**
 * When a process starts, Adama needs to pull from the data store all keys which may have a temporal
 * state machine
 */
public interface ActiveKeyStream {
  /** the data store is informing Adama of a key to load up after some time */
  public void schedule(Key key, long time);

  /** the data store has finished feeding Adama */
  public void finish();

  /** a problem emerged during the scanning */
  public void error(ErrorCodeException failure);
}
