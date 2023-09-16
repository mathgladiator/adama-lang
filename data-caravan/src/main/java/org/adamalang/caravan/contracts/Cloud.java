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
package org.adamalang.caravan.contracts;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;

import java.io.File;

/** restore/backup from the cloud */
public interface Cloud {
  /** the path for where cloud files are stored */
  File path();

  /** check the key's archive exists in the cloud */
  void exists(Key key, String archiveKey, Callback<Void> callback);

  /** restore the archive key from the cloud to a local file */
  void restore(Key key, String archiveKey, Callback<File> callback);

  /** backup the given file and send to the cloud */
  void backup(Key key, File archiveFile, Callback<Void> callback);

  /** delete the related archive key */
  void delete(Key key, String archiveKey, Callback<Void> callback);
}
