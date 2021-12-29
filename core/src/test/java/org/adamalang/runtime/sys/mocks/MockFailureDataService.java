/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.sys.mocks;

import org.adamalang.runtime.contracts.ActiveKeyStream;
import org.adamalang.common.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.common.ErrorCodeException;

public class MockFailureDataService implements DataService {
    @Override
    public void get(Key key, Callback<LocalDocumentChange> callback) {
        callback.failure(new ErrorCodeException(999));
    }

    public boolean crashScan = false;
    @Override
    public void scan(ActiveKeyStream streamback) {
        streamback.finish();
        if (crashScan) {
            streamback.error(new ErrorCodeException(231));
        }
    }

    @Override
    public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
        callback.failure(new ErrorCodeException(999));
    }

    @Override
    public void patch(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
        callback.failure(new ErrorCodeException(999));
    }

    @Override
    public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
        callback.failure(new ErrorCodeException(999));
    }

    @Override
    public void delete(Key key, Callback<Void> callback) {
        callback.failure(new ErrorCodeException(999));
    }
}
