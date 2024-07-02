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
package org.adamalang.runtime.sys;

import org.adamalang.runtime.natives.NtPrincipal;

import java.net.URL;

/** wrap common data around a request for policies to exploit */
public class CoreRequestContext {
  public final NtPrincipal who;
  public final String origin;
  public final String ip;
  public final String key;
  public final String domain;

  public CoreRequestContext(NtPrincipal who, String origin, String ip, String key) {
    this.who = who;
    this.origin = origin;
    this.ip = ip;
    this.key = key;
    String _domain = "unknown";
    try {
      _domain = new URL(origin).getHost();
    } catch (Exception ex) {
    }
    this.domain = _domain;
  }
}
