/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.json.JsonStreamWriter;

/** the base contract which messages must obey */
public interface NtMessageBase /* extends CanConvertToObject */ {
  public NtMessageBase NULL = writer -> {
    writer.beginObject();
    writer.endObject();
  };

  public void __writeOut(JsonStreamWriter writer);
}
