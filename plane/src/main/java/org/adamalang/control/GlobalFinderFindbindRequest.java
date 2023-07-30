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
import org.adamalang.Session;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.web.io.*;

/** Find the host, and if not bound then take possession */
public class GlobalFinderFindbindRequest {
  public final String space;
  public final String key;
  public final String region;
  public final String machine;

  public GlobalFinderFindbindRequest(final String space, final String key, final String region, final String machine) {
    this.space = space;
    this.key = key;
    this.region = region;
    this.machine = machine;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<GlobalFinderFindbindRequest> callback) {
    try {
      final String space = request.getStringNormalize("space", true, 9003);
      final String key = request.getString("key", true, 9004);
      final String region = request.getString("region", true, 9006);
      final String machine = request.getString("machine", true, 9005);
      nexus.executor.execute(new NamedRunnable("globalfinderfindbind-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new GlobalFinderFindbindRequest(space, key, region, machine));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("globalfinderfindbind-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("space", space);
    _node.put("key", key);
    _node.put("region", region);
    _node.put("machine", machine);
  }
}
