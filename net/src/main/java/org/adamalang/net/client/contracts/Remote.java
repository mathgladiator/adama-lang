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
package org.adamalang.net.client.contracts;

import org.adamalang.common.Callback;

/** control an active stream */
public interface Remote {
  void canAttach(Callback<Boolean> callback);

  void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback);

  void send(String channel, String marker, String message, Callback<Integer> callback);

  void password(String password, Callback<Integer> callback);

  void update(String viewerState, Callback<Void> callback);

  void disconnect();
}
