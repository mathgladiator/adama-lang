package org.adamalang.web.service.mocks;

import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;

public class MockServiceBase implements ServiceBase {
    @Override
    public ServiceConnection establish(ConnectionContext context) {
        return new ServiceConnection() {
            @Override
            public void execute(JsonRequest request, JsonResponder responder) {

            }

            @Override
            public boolean keepalive() {
                return false;
            }

            @Override
            public void kill() {

            }
        };
    }
}
