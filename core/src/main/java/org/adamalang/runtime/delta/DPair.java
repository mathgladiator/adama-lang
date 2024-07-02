/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
