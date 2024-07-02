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
package org.adamalang.translator.tree.common;

import org.adamalang.runtime.json.JsonStreamWriter;

/**
 * Defines an error within the document that can also be tied to a specific position within the
 * document
 */
public class DocumentError {
  public final String file;
  public final String message;
  public final DocumentPosition position;

  /**
   * construct the error
   * @param position where within the file the error happened
   * @param message what is the message for the error
   */
  public DocumentError(final DocumentPosition position, final String message) {
    if (position == null || message == null) {
      throw new NullPointerException();
    }
    this.file = position.getSource();
    this.message = message;
    this.position = position;
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
    writer.writeString(message);

    if (file != null) {
      writer.writeObjectFieldIntro("file");
      writer.writeString(file);
    }

    writer.endObject();
    return writer.toString();
  }
}
