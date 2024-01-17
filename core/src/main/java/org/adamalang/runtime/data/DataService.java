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
import org.adamalang.runtime.contracts.DeleteTask;

import java.util.Set;

/** the contract for the data service */
public interface DataService {

  /** Download the entire object and return the entire json */
  void get(Key key, Callback<LocalDocumentChange> callback);

  /** write the first entry for the document */
  void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback);

  /** Apply a series of patches to the document using rfc7396 */
  void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback);

  /** Compute the change the state of the document to the indicated seq by the given client */
  void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback);

  /** Delete the document given by the ID */
  void delete(Key key, DeleteTask task, Callback<Void> callback);

  /** Snapshot the state of the document */
  void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback);

  /** a command from on-high to shed the key from the machine */
  void shed(Key key);

  /** recover a document from a snapshot */
  void recover(Key key, DocumentRestore restore, Callback<Void> callback);

  /** list all the keys in storage */
  void inventory(Callback<Set<Key>> callback);

  /** close the storage */
  void close(Key key, Callback<Void> callback);
}
