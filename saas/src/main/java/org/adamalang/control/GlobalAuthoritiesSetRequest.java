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

/** Set the keystore for an authority */
public class GlobalAuthoritiesSetRequest {
  public final Integer owner;
  public final String authority;
  public final String keystore;

  public GlobalAuthoritiesSetRequest(final Integer owner, final String authority, final String keystore) {
    this.owner = owner;
    this.authority = authority;
    this.keystore = keystore;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<GlobalAuthoritiesSetRequest> callback) {
    try {
      final Integer owner = request.getInteger("owner", true, 9010);
      final String authority = request.getString("authority", true, 9011);
      final String keystore = request.getString("keystore", true, 9012);
      nexus.executor.execute(new NamedRunnable("globalauthoritiesset-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new GlobalAuthoritiesSetRequest(owner, authority, keystore));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("globalauthoritiesset-error") {
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
