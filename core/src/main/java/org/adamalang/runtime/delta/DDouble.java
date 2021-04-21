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

public class DDouble {
  private Double prior;

  public DDouble() {
    prior = null;
  }

  public void hide(final PrivateLazyDeltaWriter writer) {
    if (prior != null) {
      writer.writeNull();
      prior = null;
    }
  }

  public void show(final Double value, final PrivateLazyDeltaWriter writer) {
    if (prior == null) {
      if (value != null) {
        writer.writeDouble(value.doubleValue());
      }
    } else {
      if (value != null && value.doubleValue() != prior.doubleValue()) {
        writer.writeDouble(value.doubleValue());
      }
    }
    prior = value;
  }
}
