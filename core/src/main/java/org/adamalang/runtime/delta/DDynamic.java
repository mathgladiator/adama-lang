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
import org.adamalang.runtime.json.JsonAlgebra;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtDynamic;

/** a dynamic that will respect privacy and sends state to client only on changes */
public class DDynamic implements DeltaNode {
  private NtDynamic prior;
  private Object priorParsed;

  public DDynamic() {
    prior = null;
    priorParsed = null;
  }

  /** the dynamic tree is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (prior != null) {
      writer.writeNull();
      prior = null;
      priorParsed = null;
    }
  }

  @Override
  public void clear() {
    prior = null;
    priorParsed = null;
  }

  /** memory usage */
  @Override
  public long __memory() {
    return 2 * (prior != null ? prior.memory() : 0) + 32;
  }

  /** the dynamic tree is visible, so show changes */
  public void show(final NtDynamic value, final PrivateLazyDeltaWriter writer) {
    if (!value.equals(prior)) {
      Object parsedValue = new JsonStreamReader(value.json).readJavaTree();
      JsonAlgebra.writeObjectFieldDelta(priorParsed, parsedValue, writer.force());
      priorParsed = parsedValue;
      prior = value;
    }
  }
}
