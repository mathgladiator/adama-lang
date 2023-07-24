/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.web;

import org.adamalang.runtime.async.EphemeralFuture;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.remote.RxCache;
import org.adamalang.runtime.sys.web.partial.WebPartial;

/** an item within the queue for processing web tasks */
public class WebQueueItem {
  public final int id;
  public final WebContext context;
  public final RxCache cache;
  public final WebItem item;
  public final EphemeralFuture<WebResponse> future;
  public WebQueueState state;

  public WebQueueItem(int id, WebContext context, WebItem item, RxCache cache, EphemeralFuture<WebResponse> future) {
    this.id = id;
    this.context = context;
    this.item = item;
    this.cache = cache;
    this.future = future;
    this.state = WebQueueState.Created;
  }

  public static WebQueueItem from(int taskId, JsonStreamReader reader, RxCache cache) {
    if (reader.startObject()) {
      WebContext _context = null;
      WebPartial _item_partial = null;
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "cache":
            cache.__insert(reader);
            break;
          case "context":
            _context = WebContext.readFromObject(reader);
            break;
          case "item":
            _item_partial = WebPartial.read(reader);
            break;
          default:
            reader.skipValue();
        }
      }
      return new WebQueueItem(taskId, _context, _item_partial.convert(_context), cache, null);
    } else {
      reader.skipValue();
    }
    return null;
  }

  public void commit(int key, JsonStreamWriter forward, JsonStreamWriter reverse) {
    if (cache.__isDirty()) {
      forward.writeObjectFieldIntro("" + key);
      forward.beginObject();
      reverse.writeObjectFieldIntro("" + key);
      reverse.beginObject();
      cache.__commit("cache", forward, reverse);
      forward.endObject();
      reverse.endObject();
    }
  }

  public void dump(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("context");
    context.writeAsObject(writer);
    writer.writeObjectFieldIntro("item");
    item.writeAsObject(writer);
    writer.writeObjectFieldIntro("cache");
    cache.__dump(writer);
    writer.endObject();
  }

  public void patch(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        if ("cache".equals(reader.fieldName())) {
          cache.__patch(reader);
        } else {
          reader.skipValue();
        }
      }
    } else {
      reader.skipValue();
    }
  }
}
