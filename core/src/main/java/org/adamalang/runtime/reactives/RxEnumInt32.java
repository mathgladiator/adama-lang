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
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;

import java.util.function.Function;

/** a reactive 32-bit integer (int) used by enums with the ability to correct invalid values */
public class RxEnumInt32 extends RxInt32 {
  private final Function<Integer, Integer> fixer;

  public RxEnumInt32(RxParent parent, int value, Function<Integer, Integer> fixer) {
    super(parent, value);
    this.fixer = fixer;
  }

  @Override
  public void __insert(JsonStreamReader reader) {
    super.__insert(reader);
    this.backup = fixer.apply(this.backup);
    this.value = fixer.apply(this.value);
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readInteger());
  }

  @Override
  public void forceSet(int id) {
    super.forceSet(fixer.apply(id));
  }

  @Override
  public void set(Integer value) {
    super.set(fixer.apply(value));
  }
}
