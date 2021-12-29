/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.stdlib.IdCodec;

/** an asset that will respect privacy and sends state to client only on changes */
public class DAsset {
  private NtAsset prior;

  public DAsset() {
    prior = null;
  }

  /** the asset is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (prior != null) {
      writer.writeNull();
      prior = null;
    }
  }

  /** the asset is visible, so show changes */
  public void show(final NtAsset value, final PrivateLazyDeltaWriter writer) {
    if (prior == null || !value.equals(prior)) {
      final var obj = writer.planObject();
      // note; we don't send the name as that may leak private information from the uploader
      obj.planField("id").writeString(value.id);
      obj.planField("size").writeFastString("" + value.size);
      obj.planField("type").writeString(value.contentType);
      obj.planField("md5").writeString(value.md5);
      obj.planField("sha384").writeString(value.sha384);
      obj.end();
    }
    prior = value;
  }
}
