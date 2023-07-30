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

/** Get an authority */
public class GlobalAuthoritiesGetPublicRequest {
  public final String authority;

  public GlobalAuthoritiesGetPublicRequest(final String authority) {
    this.authority = authority;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<GlobalAuthoritiesGetPublicRequest> callback) {
    try {
      final String authority = request.getString("authority", true, 9011);
      nexus.executor.execute(new NamedRunnable("globalauthoritiesgetpublic-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new GlobalAuthoritiesGetPublicRequest(authority));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("globalauthoritiesgetpublic-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("authority", authority);
  }
}
