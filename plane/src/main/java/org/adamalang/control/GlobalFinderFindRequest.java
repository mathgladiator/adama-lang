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

/** Find the host for a given document */
public class GlobalFinderFindRequest {
  public final String space;
  public final String key;

  public GlobalFinderFindRequest(final String space, final String key) {
    this.space = space;
    this.key = key;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<GlobalFinderFindRequest> callback) {
    try {
      final String space = request.getStringNormalize("space", true, 9003);
      final String key = request.getString("key", true, 9004);
      nexus.executor.execute(new NamedRunnable("globalfinderfind-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new GlobalFinderFindRequest(space, key));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("globalfinderfind-error") {
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
  }
}
