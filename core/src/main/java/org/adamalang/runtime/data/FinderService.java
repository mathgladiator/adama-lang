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
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;

import java.util.List;

/** an interface to describe how to find a specific document by key */
public interface FinderService extends SimpleFinderService {

  /** take over for the key */
  void bind(Key key, Callback<Void> callback);

  /** release the machine for the given key */
  void free(Key key, Callback<Void> callback);

  /** set a backup copy while still active on machine */
  void backup(Key key, BackupResult result, Callback<Void> callback);

  /** mark the key for deletion */
  void markDelete(Key key, Callback<Void> callback);

  /** signal that deletion has been completed */
  void commitDelete(Key key, Callback<Void> callback);

  /** list all items on a host */
  void list(Callback<List<Key>> callback);

  /** list all items on a host */
  void listDeleted(Callback<List<Key>> callback);

}
