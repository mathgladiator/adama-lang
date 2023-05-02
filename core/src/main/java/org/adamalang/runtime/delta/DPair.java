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

/** a cached pair of a key and a value for map results and other pairing; a delta version of NtPair */
public class DPair<dTyIn extends DeltaNode, dTyOut extends DeltaNode> implements DeltaNode {
  private dTyIn priorKey;
  private dTyOut priorValue;

  public DPair() {
    priorKey = null;
    priorValue = null;
  }

  /** the double is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (priorKey != null) {
      writer.writeNull();
      priorKey = null;
      priorValue = null;
    }
  }

  public dTyIn key(Supplier<dTyIn> make) {
    if (priorKey == null) {
      priorKey = make.get();
    }
    return priorKey;
  }

  public dTyOut value(Supplier<dTyOut> make) {
    if (priorValue == null) {
      priorValue = make.get();
    }
    return priorValue;
  }

  @Override
  public void clear() {
    priorKey = null;
    priorValue = null;
  }

  @Override
  public long __memory() {
    return 16 + (priorKey != null ? priorKey.__memory() : 0) + (priorValue != null ? priorValue.__memory() : 0);
  }
}
