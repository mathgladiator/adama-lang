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
package org.adamalang.runtime.sys;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtMessageBase;

public class AuthResponse {
  public String hash;
  public String agent;
  public String channel;
  public String success;

  public AuthResponse() {
    this.hash = null;
    this.agent = null;
    this.channel = null;
    this.success = null;
  }

  public AuthResponse hash(String hash) {
    this.hash = hash;
    return this;
  }

  public AuthResponse agent(String agent) {
    this.agent = agent;
    return this;
  }

  public AuthResponse channel(String channel) {
    this.channel = channel;
    return this;
  }

  public AuthResponse success(NtMessageBase message) {
    JsonStreamWriter writer = new JsonStreamWriter();
    message.__writeOut(writer);
    this.success = writer.toString();
    return this;
  }
}
