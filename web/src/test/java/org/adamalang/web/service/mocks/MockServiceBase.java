/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
