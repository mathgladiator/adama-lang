/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.control;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.connection.Session;
import org.adamalang.web.io.*;

/** Start the service up */
public class MachineStartRequest {
  public final String machineIdentity;
  public final String role;

  public MachineStartRequest(final String machineIdentity, final String role) {
    this.machineIdentity = machineIdentity;
    this.role = role;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<MachineStartRequest> callback) {
    try {
      final String machineIdentity = request.getString("machine-identity", true, 9001);
      final String role = request.getString("role", true, 9002);
      nexus.executor.execute(new NamedRunnable("machinestart-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new MachineStartRequest(machineIdentity, role));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("machinestart-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
  }
}
