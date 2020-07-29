/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamWriter;

/** a reactive string */
public class RxFastString extends RxString {
  public RxFastString(final RxParent owner, final String value) {
    super(owner, value);
  }

  @Override
  public void __commit(final String name, final JsonStreamWriter writer) {
    if (__isDirty()) {
      writer.writeObjectFieldIntro(name);
      writer.writeFastString(value);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeFastString(value);
  }
}
