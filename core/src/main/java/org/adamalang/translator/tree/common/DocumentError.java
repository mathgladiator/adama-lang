/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.common;

import org.adamalang.runtime.json.JsonStreamWriter;

/** Defines an error within the document that can also be tied to a specific
 * position within the document */
public class DocumentError {
  public final String message;
  public final DocumentPosition position;
  public final String tutorial;

  /** construct the error
   *
   * @param position where within the file the error happened
   * @param message  what is the message for the error */
  public DocumentError(final DocumentPosition position, final String message, final String tutorial) {
    if (position == null || message == null) { throw new NullPointerException(); }
    this.message = message;
    this.position = position;
    this.tutorial = tutorial;
  }

  /** write the error out into the given ObjectNode using the LSP format */
  public String json() {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("range");
    position.dump(writer);

    writer.writeObjectFieldIntro("severity");
    writer.writeInteger(1);

    writer.writeObjectFieldIntro("source");
    writer.writeString("error");

    writer.writeObjectFieldIntro("message");
    writer.writeString(tutorial == null ? message : message + " (" + tutorial + ")");
    writer.endObject();
    return writer.toString();
  }
}
