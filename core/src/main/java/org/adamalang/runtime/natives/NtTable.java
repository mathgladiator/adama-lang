/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
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
