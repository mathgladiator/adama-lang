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
import org.adamalang.connection.Session;
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

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<AttachmentAppendRequest> callback) {
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
