/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.frontend.Session;
import org.adamalang.web.io.*;

/** Here, we ask if the connection if it has an asset key already.
  * If not, then it will generate one and send it along.
  * Otherwise, it will return the key bound to the connection.
  * 
  * This is allows anyone to have access to assets which are not exposed directly via a web handler should they see the asset within their document view. */
public class ConfigureMakeOrGetAssetKeyRequest {

  public ConfigureMakeOrGetAssetKeyRequest() {
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<ConfigureMakeOrGetAssetKeyRequest> callback) {
    nexus.executor.execute(new NamedRunnable("configuremakeorgetassetkey-error") {
      @Override
        public void execute() throws Exception {
          callback.success(new ConfigureMakeOrGetAssetKeyRequest());
        }
      });
  }

  public void logInto(ObjectNode _node) {
  }
}
