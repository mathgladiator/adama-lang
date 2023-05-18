/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys.web;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

/** a web item persisted */
public interface WebItem {

  /** load the web item from an object (most likely in a queue) */
  static WebItem read(WebContext context, JsonStreamReader reader) {
    WebItem result = null;
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        if ("put".equals(reader.fieldName())) {
          result = WebPut.read(context, reader);
        } else {
          reader.skipValue();
        }
      }
    }
    return result;
  }

  /** write the item as a field within another object */
  void writeAsObject(JsonStreamWriter writer);
}
