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

public class DString {
  private String prior;

  public DString() {
    prior = null;
  }

  public void hide(final PrivateLazyDeltaWriter writer) {
    if (prior != null) {
      writer.writeNull();
      prior = null;
    }
  }

  public void show(final String value, final PrivateLazyDeltaWriter writer) {
    if (prior == null) {
      if (value != null) {
        writer.writeString(value);
      }
    } else {
      if (value != null && !value.equals(prior)) {
        writer.writeString(value);
      }
    }
    prior = value;
  }
}
