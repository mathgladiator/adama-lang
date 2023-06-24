/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.*;

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
  public void delete(Key key, Callback<Void> callback) {
    callback.failure(new ErrorCodeException(999));
  }

  @Override
  public void snapshot(Key key, DocumentSnapshot snapshot, Callback<Integer> callback) {
    callback.failure(new ErrorCodeException(912));
  }

  @Override
  public void close(Key key, Callback<Void> callback) {
    callback.failure(new ErrorCodeException(1231));
  }
}
