package org.adamalang.web.extern;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.io.AsyncTransform;

public class Authenticator implements AsyncTransform<String, NtClient> {
    @Override
    public void execute(String parameter, Callback<NtClient> callback) {
        callback.success(NtClient.NO_ONE);
    }
}
