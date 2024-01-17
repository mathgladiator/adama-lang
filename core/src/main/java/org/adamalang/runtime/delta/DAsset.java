/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
import org.adamalang.runtime.natives.NtAsset;

/** an asset that will respect privacy and sends state to client only on changes */
public class DAsset implements DeltaNode {
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

  @Override
  public void clear() {
    prior = null;
  }

  /** memory usage */
  @Override
  public long __memory() {
    return (prior != null ? prior.memory() : 0) + 32;
  }

  /** the asset is visible, so show changes */
  public void show(final NtAsset value, final PrivateLazyDeltaWriter writer) {
    if (!value.equals(prior)) {
      final var obj = writer.planObject();
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
