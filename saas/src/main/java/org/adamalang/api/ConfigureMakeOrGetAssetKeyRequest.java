/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.connection.Session;
import org.adamalang.web.io.*;

/** Here, we ask if the connection if it has an asset key already.
  * If not, then it will generate one and send it along.
  * Otherwise, it will return the key bound to the connection. */
public class ConfigureMakeOrGetAssetKeyRequest {

  public ConfigureMakeOrGetAssetKeyRequest() {
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<ConfigureMakeOrGetAssetKeyRequest> callback) {
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
