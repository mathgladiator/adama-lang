/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<ConfigureMakeOrGetAssetKeyRequest> callback) {
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
