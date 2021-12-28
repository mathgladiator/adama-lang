package org.adamalang.web.service.mocks;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;

public class MockServiceBase implements ServiceBase {
    @Override
    public ServiceConnection establish(ConnectionContext context) {
        return new ServiceConnection() {
            boolean alive = true;
            @Override
            public void execute(JsonRequest request, JsonResponder responder) {
                try {
                    switch (request.method()) {
                        case "cake": {
                            responder.stream("{\"boss\":1}");
                            responder.finish("{\"boss\":2}");
                            return;
                        }
                        case "kill": {
                            responder.stream("{\"death\":1}");
                            alive = false;
                            return;
                        }
                        case "ex": {
                            responder.error(new ErrorCodeException(1234));
                            return;
                        }
                    }

                } catch (ErrorCodeException ex) {
                    responder.error(ex);
                }
            }

            @Override
            public boolean keepalive() {
                return alive;
            }

            @Override
            public void kill() {

            }
        };
    }
}
