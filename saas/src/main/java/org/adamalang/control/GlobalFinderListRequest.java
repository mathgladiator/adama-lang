/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.control;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.connection.Session;
import org.adamalang.web.io.*;

/** List the keys on the given machine */
public class GlobalFinderListRequest {
  public final String region;
  public final String machine;

  public GlobalFinderListRequest(final String region, final String machine) {
    this.region = region;
    this.machine = machine;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<GlobalFinderListRequest> callback) {
    try {
      final String region = request.getString("region", true, 9006);
      final String machine = request.getString("machine", true, 9005);
      nexus.executor.execute(new NamedRunnable("globalfinderlist-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new GlobalFinderListRequest(region, machine));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("globalfinderlist-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("region", region);
    _node.put("machine", machine);
  }
}
