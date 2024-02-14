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
package org.adamalang.runtime.natives;

import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.natives.algo.CanWriteCSV;
import org.adamalang.runtime.natives.algo.MessageCSVWriter;
import org.adamalang.runtime.natives.lists.ArrayNtList;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

/** a table defined within code */
public class NtTable<Ty extends NtMessageBase> implements RxChild {
  private final ArrayList<Ty> items;
  private final Supplier<Ty> maker;

  public NtTable(final NtTable<Ty> other) {
    this.maker = other.maker;
    this.items = new ArrayList<>(other.items);
  }

  public NtTable(final Supplier<Ty> maker) {
    this.maker = maker;
    this.items = new ArrayList<>();
  }

  @Override
  public boolean __raiseInvalid() {
    return true;
  }

  public void delete() {
    items.clear();
  }

  public NtList<Ty> iterate(final boolean done) {
    return new ArrayNtList<>(items);
  }

  public Ty make() {
    final var item = maker.get();
    items.add(item);
    return item;
  }

  public int size() {
    return items.size();
  }

  public String to_csv(String header, Function<Ty, CanWriteCSV> cast) {
    StringBuilder sb = new StringBuilder();
    sb.append(header);
    for (Ty item : items) {
      sb.append("\r\n");
      MessageCSVWriter writer = new MessageCSVWriter();
      cast.apply(item).__write_csv_row(writer);
      sb.append(writer.toString());
    }
    return sb.toString();
  }
}
