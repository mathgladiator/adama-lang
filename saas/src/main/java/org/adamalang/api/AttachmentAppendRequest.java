package org.adamalang.api;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

/**  */
public class AttachmentAppendRequest {
  public final Long upload;
  public final String chunkMd5;
  public final String base64Bytes;

  public AttachmentAppendRequest(final Long upload, final String chunkMd5, final String base64Bytes) {
    this.upload = upload;
    this.chunkMd5 = chunkMd5;
    this.base64Bytes = base64Bytes;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AttachmentAppendRequest> callback) {
    try {
      final Long upload = request.getLong("upload", true, 409609);
      final String chunkMd5 = request.getString("chunk-md5", true, 462859);
      final String base64Bytes = request.getString("base64-bytes", true, 409608);
      nexus.executor.execute(() -> {
        callback.success(new AttachmentAppendRequest(upload, chunkMd5, base64Bytes));
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
