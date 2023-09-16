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
import org.adamalang.runtime.natives.NtResult;

import java.util.function.Supplier;

/** a maybe wrapper that will respect privacy and sends state to client only on changes */
public class DResult<dTy extends DeltaNode> implements DeltaNode {
  private dTy cache;
  private Boolean failed;
  private String message;
  private Integer code;

  public DResult() {
    this.cache = null;
    this.failed = null;
    this.message = null;
    this.code = null;
  }

  /** get or make the cached delta (see CodeGenDeltaClass) */
  public dTy get(final Supplier<dTy> maker) {
    if (cache == null) {
      cache = maker.get();
    }
    return cache;
  }

  /** start showing the result(s) */
  public PrivateLazyDeltaWriter show(NtResult<?> result, PrivateLazyDeltaWriter writer) {
    final var obj = writer.planObject();
    if (failed == null || result.failed() != failed) {
      obj.planField("failed").writeBool(result.failed());
      this.failed = result.failed();
    }
    if (message == null || !result.message().equals(message)) {
      obj.planField("message").writeString(result.message());
      this.message = result.message();
    }
    if (code == null || result.code() != code) {
      obj.planField("code").writeInt(result.code());
      this.code = result.code();
    }
    return obj.planField("result");
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
