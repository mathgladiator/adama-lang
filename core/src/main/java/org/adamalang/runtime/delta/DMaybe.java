/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.contracts.DeltaNode;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;

import java.util.function.Supplier;

/** a maybe wrapper that will respect privacy and sends state to client only on changes */
public class DMaybe<dTy extends DeltaNode> implements DeltaNode {
  private dTy cache;

  /** get or make the cached delta (see CodeGenDeltaClass) */
  public dTy get(final Supplier<dTy> maker) {
    if (cache == null) {
      cache = maker.get();
    }
    return cache;
  }

  /** the maybe is either no longer visible (was made private or isn't present) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (cache != null) {
      writer.writeNull();
      cache = null;
    }
  }

  @Override
  public void clear() {
    cache = null;
  }

  /** memory usage */
  @Override
  public long __memory() {
    return 40 + (cache != null ? cache.__memory() : 0);
  }
}
