/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import java.util.ArrayList;
import org.adamalang.runtime.bridges.MessageBridge;
import org.adamalang.runtime.contracts.Bridge;
import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.natives.lists.ArrayNtList;
import org.adamalang.runtime.stdlib.Utility;

/** a table defined within code */
public class NtTable<Ty extends NtMessageBase> implements RxChild {
  private final Bridge<Ty> bridge;
  private final ArrayList<Ty> items;

  public NtTable(final MessageBridge<Ty> bridge) {
    this.bridge = bridge;
    this.items = new ArrayList<>();
  }

  public NtTable(final NtTable<Ty> other) {
    this.bridge = other.bridge;
    this.items = new ArrayList<>(other.items);
  }

  @Override
  public boolean __raiseInvalid() {
    return true;
  }

  public void delete() {
    items.clear();
  }

  public NtList<Ty> iterate(final boolean done) {
    return new ArrayNtList<>(items, bridge);
  }

  public Ty make() {
    final var item = bridge.fromJsonNode(Utility.createObjectNode());
    items.add(item);
    return item;
  }

  public int size() {
    return items.size();
  }
}
