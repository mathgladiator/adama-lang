/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.commands.contracts;

import org.adamalang.runtime.exceptions.ErrorCodeException;

/** how commands respond */
public interface CommandResponder {

  /** stream an update */
  public void stream(String json);

  /** respond in a terminal fashion */
  public void finish(String json);

  /** respond with a terminal error */
  public void error(ErrorCodeException ex);
}
