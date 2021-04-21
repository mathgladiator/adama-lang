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

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.util.function.Consumer;

/** how commands respond */
public interface CommandResponder {

  /** stream an update */
  public void stream(String json);

  /** respond in a terminal fashion */
  public void finish(String json);

  /** respond with a terminal error */
  public void error(ErrorCodeException ex);

  public static <T> Callback<T> TO_CALLBACK(Consumer<T> consumer, CommandResponder responder) {
    return new Callback<T>() {
      @Override
      public void success(T value) {
        consumer.accept(value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    };
  }
}
