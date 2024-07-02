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
