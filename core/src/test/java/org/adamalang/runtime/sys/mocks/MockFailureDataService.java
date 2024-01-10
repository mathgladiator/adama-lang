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
package org.adamalang.runtime.sys.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.DeleteTask;
import org.adamalang.runtime.data.*;

import java.util.Set;

public class MockFailureDataService implements DataService {
  public boolean crashScan = false;

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    callback.failure(new ErrorCodeException(999));
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    callback.failure(new ErrorCodeException(999));
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    callback.failure(new ErrorCodeException(999));
  }

  @Override
  public void compute(
      Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    callback.failure(new ErrorCodeException(999));
  }

  @Override
  public void delete(Key key, DeleteTask task, Callback<Void> callback) {
    callback.failure(new ErrorCodeException(999));
  }

  @Override
  public void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback) {
    callback.failure(new ErrorCodeException(912));
  }

  @Override
  public void shed(Key key) {
  }

  @Override
  public void recover(Key key, DocumentRestore restore, Callback<Void> callback) {
    callback.failure(new ErrorCodeException(-42));
  }

  @Override
  public void inventory(Callback<Set<Key>> callback) {
    callback.failure(new ErrorCodeException(123));
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    callback.failure(new ErrorCodeException(1231));
  }
}
