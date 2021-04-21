/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.PrivateLazyDeltaWriter;

public class DBoolean {
  private Boolean prior;

  public DBoolean() {
    prior = null;
  }

  public void hide(final PrivateLazyDeltaWriter writer) {
    if (prior != null) {
      writer.writeNull();
      prior = null;
    }
  }

  public void show(final Boolean value, final PrivateLazyDeltaWriter writer) {
    if (prior == null) {
      if (value != null) {
        writer.writeBool(value.booleanValue());
      }
    } else {
      if (value != null && value.booleanValue() != prior.booleanValue()) {
        writer.writeBool(value.booleanValue());
      }
    }
    prior = value;
  }
}
