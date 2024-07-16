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
import org.adamalang.runtime.remote.replication.RxReplicationStatus;

/** an asset that will respect privacy and sends state to client only on changes */
public class DReplicationStatus implements DeltaNode {
  String prior;

  public DReplicationStatus() {
    this.prior = null;
  }

  /** the asset is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (prior != null) {
      writer.writeNull();
      prior = null;
    }
  }

  @Override
  public void clear() {
    prior = null;
  }

  /** memory usage */
  @Override
  public long __memory() {
    return 40 + (prior != null ? prior.length() * 2L : 0);
  }

  /**show the value */
  public void show(final RxReplicationStatus status, final PrivateLazyDeltaWriter writer) {
    String value = status.toString();
    if (!value.equals(prior)) {
      writer.writeString(value);
    }
    prior = value;
  }
}
