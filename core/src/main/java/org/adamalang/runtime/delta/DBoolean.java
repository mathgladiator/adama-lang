/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
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
