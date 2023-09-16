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

/** This is an internal API used only by Adama for multi-region support.
  * 
  * Start an upload for the given document with the given filename and content type. */
public class ConnectionAttachRequest {
  public final Long connection;
  public final String assetId;
  public final String filename;
  public final String contentType;
  public final Long size;
  public final String digestMd5;
  public final String digestSha384;

  public ConnectionAttachRequest(final Long connection, final String assetId, final String filename, final String contentType, final Long size, final String digestMd5, final String digestSha384) {
    this.connection = connection;
    this.assetId = assetId;
    this.filename = filename;
    this.contentType = contentType;
    this.size = size;
    this.digestMd5 = digestMd5;
    this.digestSha384 = digestSha384;
  }

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<ConnectionAttachRequest> callback) {
    try {
      final Long connection = request.getLong("connection", true, 405505);
      final String assetId = request.getString("asset-id", true, 476156);
      final String filename = request.getString("filename", true, 470028);
      final String contentType = request.getString("content-type", true, 455691);
      final Long size = request.getLong("size", true, 477179);
      final String digestMd5 = request.getString("digest-md5", true, 445437);
      final String digestSha384 = request.getString("digest-sha384", true, 406525);
      nexus.executor.execute(new NamedRunnable("connectionattach-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new ConnectionAttachRequest(connection, assetId, filename, contentType, size, digestMd5, digestSha384));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("connectionattach-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("asset-id", assetId);
    _node.put("filename", filename);
    _node.put("content-type", contentType);
    _node.put("size", size);
    _node.put("digest-md5", digestMd5);
    _node.put("digest-sha384", digestSha384);
  }
}
