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
package org.adamalang.rxhtml.routing;

import java.util.Map;
import java.util.TreeMap;

/** a simple static target for HTTP */
public class Target {
  public final int status;
  public final TreeMap<String, String> headers;
  public final byte[] body;
  private final long memory;

  public Target(int status, TreeMap<String, String> headers, byte[] body) {
    this.status = status;
    this.headers = headers;
    this.body = body;
    long _memory = 64;
    if (headers != null) {
      for(Map.Entry<String, String> entry : headers.entrySet()) {
        _memory += 64 + entry.getKey().length() + entry.getValue().length();
      }
    }
    if (body != null) {
      _memory += 64 + body.length;
    }
    this.memory = _memory;
  }

  public long memory() {
    return memory;
  }
}
