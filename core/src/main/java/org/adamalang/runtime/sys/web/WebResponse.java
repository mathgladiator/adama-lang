/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.adamalang.translator.tree.definitions.web.WebGuard;

public class WebResponse {
  public String contentType = null;
  public String body = null;
  public NtAsset asset = null;
  public boolean cors = false;
  public int cache_ttl_seconds = 0;
  public String asset_transform = null;
  public int status;

  public WebResponse() {
    this.status = 200;
  }

  public static WebResponse readFromObject(JsonStreamReader reader) {
    if (reader.startObject()) {
      WebResponse response = new WebResponse();
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "content-type":
            response.contentType = reader.readString();
            break;
          case "body":
            response.body = reader.readString();
            break;
          case "asset":
            response.asset = reader.readNtAsset();
            break;
          case "asset-transform":
            response.asset_transform = reader.readString();
            break;
          case "cors":
            response.cors = reader.readBoolean();
            break;
          case "cache-ttl-seconds":
            response.cache_ttl_seconds = reader.readInteger();
            break;
          case "status":
            response.status = reader.readInteger();
            break;
          default:
            reader.skipValue();
        }
      }
      return response;
    } else {
      reader.skipValue();
    }
    return null;
  }

  public void writeAsObject(JsonStreamWriter writer) {
    writer.beginObject();
    if (contentType != null) {
      writer.writeObjectFieldIntro("content-type");
      writer.writeString(contentType);
    }
    if (body != null) {
      writer.writeObjectFieldIntro("body");
      writer.writeString(body);
    }
    if (asset != null) {
      writer.writeObjectFieldIntro("asset");
      writer.writeNtAsset(asset);
    }
    if (asset_transform != null && asset_transform.length() > 0) {
      writer.writeObjectFieldIntro("asset-transform");
      writer.writeString(asset_transform);
    }
    if (cors) {
      writer.writeObjectFieldIntro("cors");
      writer.writeBoolean(cors);
    }
    if (cache_ttl_seconds > 0) {
      writer.writeObjectFieldIntro("cache-ttl-seconds");
      writer.writeInteger(cache_ttl_seconds);
    }
    if (status != 0) {
      writer.writeObjectFieldIntro("status");
      writer.writeInteger(status);
    }
    writer.endObject();
  }

  public WebResponse html(String body) {
    this.contentType = "text/html; charset=utf-8";
    this.body = body;
    return this;
  }

  public WebResponse identity(String identity) {
    this.contentType = "text/identity";
    this.body = identity;
    return this;
  }

  public WebResponse redirect(String location) {
    this.contentType = "redirection/301";
    this.body = location;
    return this;
  }

  public WebResponse forward(String location) {
    this.contentType = "redirection/302";
    this.body = location;
    return this;
  }

  public WebResponse js(String body) {
    this.contentType = "text/javascript";
    this.body = body;
    return this;
  }

  public WebResponse error(String errorMessage) {
    this.contentType = "text/error";
    this.body = errorMessage;
    return this;
  }

  public WebResponse sign(String agent) {
    this.contentType = "text/agent";
    this.body = agent;
    return this;
  }

  public WebResponse css(String body) {
    this.contentType = "text/css";
    this.body = body;
    return this;
  }

  public WebResponse csv(String body) {
    this.contentType = "text/csv";
    this.body = body;
    return this;
  }

  public WebResponse cors(boolean cors) {
    this.cors = cors;
    return this;
  }

  public WebResponse cache_ttl_seconds(int cache_ttl_seconds) {
    this.cache_ttl_seconds = cache_ttl_seconds;
    return this;
  }

  public WebResponse asset_transform(String asset_transform) {
    this.asset_transform = asset_transform;
    return this;
  }

  public WebResponse xml(String body) {
    this.contentType = "application/xml";
    this.body = body;
    return this;
  }

  public WebResponse json(NtMessageBase message) {
    this.contentType = "application/json";
    JsonStreamWriter writer = new JsonStreamWriter();
    message.__writeOut(writer);
    this.body = writer.toString();
    return this;
  }

  public WebResponse json(NtDynamic message) {
    this.contentType = "application/json";
    this.body = message.json;
    return this;
  }

  public WebResponse asset(NtAsset asset) {
    this.contentType = asset.contentType;
    this.asset = asset;
    return this;
  }

  public WebResponse status(int status) {
    this.status = status;
    return this;
  }

  public void account(PredictiveInventory inventory) {
    if (this.body != null) {
      inventory.bandwidth(body.length());
    }
    if (this.asset != null) {
      inventory.bandwidth(this.asset.size);
    }
  }

  public static final WebResponse FORBIDDEN = new WebResponse().status(403);
}
