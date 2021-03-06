/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.natives;

import java.util.ArrayList;
import java.util.function.Supplier;
import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.natives.lists.ArrayNtList;

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
}
