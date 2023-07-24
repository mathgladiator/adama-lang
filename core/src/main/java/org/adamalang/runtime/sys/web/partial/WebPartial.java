/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.web.partial;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.sys.web.WebContext;
import org.adamalang.runtime.sys.web.WebItem;

public interface WebPartial {
  public WebItem convert(WebContext context);

  /** load the web item from an object (most likely in a queue) */
  static WebPartial read(JsonStreamReader reader) {
    WebPartial result = null;
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        if ("put".equals(reader.fieldName())) {
          result = WebPutPartial.read(reader);
        } else {
          reader.skipValue();
        }
      }
    }
    return result;
  }
}
