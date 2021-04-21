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
import org.adamalang.runtime.natives.NtClient;

public class DClient {
  private NtClient prior;

  public DClient() {
    prior = null;
  }

  public void hide(final PrivateLazyDeltaWriter writer) {
    if (prior != null) {
      writer.writeNull();
      prior = null;
    }
  }

  public void show(final NtClient value, final PrivateLazyDeltaWriter writer) {
    if (prior == null) {
      if (value != null) {
        prior = value;
        writeOut(writer);
      }
    } else {
      if (value != null && !value.equals(prior)) {
        prior = value;
        writeOut(writer);
      }
    }
  }

  private void writeOut(final PrivateLazyDeltaWriter writer) {
    final var obj = writer.planObject();
    obj.planField("agent").writeFastString(prior.agent);
    obj.planField("authority").writeFastString(prior.authority);
    obj.end();
  }
}
