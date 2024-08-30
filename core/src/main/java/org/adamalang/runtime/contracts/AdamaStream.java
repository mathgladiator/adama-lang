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
package org.adamalang.runtime.contracts;

import org.adamalang.common.Callback;

/** interface for a signel Adama stream/connection to a single document */
public interface AdamaStream {

  /** update the viewer state */
  void update(String newViewerState, Callback<Void> callback);

  /** send the document a message on the given channel */
  void send(String channel, String marker, String message, Callback<Integer> callback);

  /** send a password to the document for this person */
  void password(String password, Callback<Integer> callback);

  /** can attachments be made with the owner of the connection */
  void canAttach(Callback<Boolean> callback);

  /** attach an asset to the stream */
  void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback);

  /** close the stream */
  void close();
}
