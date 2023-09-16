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

/** Append a chunk with an MD5 to ensure data integrity. */
public class AttachmentAppendRequest {
  public final Long upload;
  public final String chunkMd5;
  public final String base64Bytes;

  public AttachmentAppendRequest(final Long upload, final String chunkMd5, final String base64Bytes) {
    this.upload = upload;
    this.chunkMd5 = chunkMd5;
    this.base64Bytes = base64Bytes;
  }

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<AttachmentAppendRequest> callback) {
    try {
      final Long upload = request.getLong("upload", true, 409609);
      final String chunkMd5 = request.getString("chunk-md5", true, 462859);
      final String base64Bytes = request.getString("base64-bytes", true, 409608);
      nexus.executor.execute(new NamedRunnable("attachmentappend-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new AttachmentAppendRequest(upload, chunkMd5, base64Bytes));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("attachmentappend-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
  }
}
