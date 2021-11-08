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

/** a int32 that will respect privacy and sends state to client only on changes */
public class DInt32 {
  private Integer prior;

  public DInt32() {
    prior = null;
  }

  /** the int32 is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (prior != null) {
      writer.writeNull();
      prior = null;
    }
  }

  /** the int32 is visible, so show changes */
  public void show(final int value, final PrivateLazyDeltaWriter writer) {
    if (prior == null || value != prior.intValue()) {
      writer.writeInt(value);
    }
    prior = value;
  }
}
