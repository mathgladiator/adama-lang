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
package org.adamalang.web.service;

/** for URIs of the for /key/uri ; this will parse out the key */
public class KeyPrefixUri {
  public final String key;
  public final String uri;

  public KeyPrefixUri(String key, String uri) {
    this.key = key;
    this.uri = uri;
  }

  public static KeyPrefixUri fromCompleteUri(String uri) {
    int offset = uri.charAt(0) == '/' ? 1 : 0;
    int slashIndex = uri.indexOf('/', offset);
    if (slashIndex > offset) {
      return new KeyPrefixUri(uri.substring(offset, slashIndex), uri.substring(slashIndex));
    } else {
      return new KeyPrefixUri(uri.substring(offset), "/");
    }
  }
}
