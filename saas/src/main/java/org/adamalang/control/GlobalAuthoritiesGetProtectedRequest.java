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

/** Get an authority */
public class GlobalAuthoritiesGetProtectedRequest {
  public final Integer owner;
  public final String authority;

  public GlobalAuthoritiesGetProtectedRequest(final Integer owner, final String authority) {
    this.owner = owner;
    this.authority = authority;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<GlobalAuthoritiesGetProtectedRequest> callback) {
    try {
      final Integer owner = request.getInteger("owner", true, 9010);
      final String authority = request.getString("authority", true, 9011);
      nexus.executor.execute(new NamedRunnable("globalauthoritiesgetprotected-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new GlobalAuthoritiesGetProtectedRequest(owner, authority));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("globalauthoritiesgetprotected-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("owner", owner);
    _node.put("authority", authority);
  }
}
