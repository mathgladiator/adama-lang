/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.PrivateLazyDeltaWriter;

/** a string that will respect privacy and sends state to client only on changes */
public class DString {
  private String prior;

  public DString() {
    prior = null;
  }

  /** the string is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (prior != null) {
      writer.writeNull();
      prior = null;
    }
  }

  /** the fast-string is visible, so show changes */
  public void show(final String value, final PrivateLazyDeltaWriter writer) {
    if (prior == null || !value.equals(prior)) {
      writer.writeString(value);
    }
    prior = value;
  }
}
