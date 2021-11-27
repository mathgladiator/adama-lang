package org.adamalang.runtime.sys.mocks;

import org.adamalang.runtime.contracts.ActiveKeyStream;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;

public class MockFailureDataService implements DataService {
    @Override
    public void get(Key key, Callback<LocalDocumentChange> callback) {
        callback.failure(new ErrorCodeException(999));
    }

    @Override
    public void scan(ActiveKeyStream streamback) {
        streamback.finish();
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
