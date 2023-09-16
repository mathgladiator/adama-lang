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

public class PrefixSplitDataService implements DataService {
  private final DataService dataServiceA;
  private final String prefixB;
  private final DataService dataServiceB;

  public PrefixSplitDataService(DataService dataServiceA, String prefixB, DataService dataServiceB) {
    this.dataServiceA = dataServiceA;
    this.prefixB = prefixB;
    this.dataServiceB = dataServiceB;
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    ds(key).get(key, callback);
  }

  public DataService ds(Key key) {
    if (key.key.startsWith(prefixB)) {
      return dataServiceB;
    }
    return dataServiceA;
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    ds(key).initialize(key, patch, callback);
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    ds(key).patch(key, patches, callback);
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    ds(key).compute(key, method, seq, callback);
  }

  @Override
  public void delete(Key key, DeleteTask task, Callback<Void> callback) {
    ds(key).delete(key, task, callback);
  }

  @Override
  public void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback) {
    ds(key).snapshot(key, snapshot, callback);
  }

  @Override
  public void shed(Key key) {
    ds(key).shed(key);
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    ds(key).close(key, callback);
  }
}
