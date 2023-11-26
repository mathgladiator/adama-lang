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
package org.adamalang.web.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Validators;

/** simple parser for a /SPACE/KEY/URI... request */
public class SpaceKeyRequest {
  public final String space;
  public final String key;
  public final String uri;

  public SpaceKeyRequest(String space, String key, String uri) {
    this.space = space;
    this.key = key;
    this.uri = uri;
  }

  public static SpaceKeyRequest parse(String uri) {
    int firstSlash = uri.indexOf('/');
    if (firstSlash >= 0) {
      int secondSlash = uri.indexOf('/', firstSlash + 1);
      if (secondSlash >= 0) {
        String space = uri.substring(firstSlash + 1, secondSlash);
        if (Validators.simple(space, 127)) {
          int third = uri.indexOf('/', secondSlash + 1);
          String key = third >= 0 ? uri.substring(secondSlash + 1, third) : uri.substring(secondSlash + 1);
          if (Validators.simple(key, 511)) {
            return new SpaceKeyRequest(space, key, third >= 0 ? uri.substring(third) : "/");
          }
        }
      }
    }
    return null;
  }

  public void logInto(ObjectNode logItem) {
    logItem.put("space", space);
    logItem.put("uri", uri);
    logItem.put("key", key);
  }

  public String cacheKey(String parameters) {
    return space + "/" + key + "/" + uri + "?" + parameters;
  }
}
