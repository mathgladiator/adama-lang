package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.io.*;

/**  */
public class AttachmentFinishRequest {
  public final Long upload;
  public final String md5;

  public AttachmentFinishRequest(final Long upload, final String md5) {
    this.upload = upload;
    this.md5 = md5;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AttachmentFinishRequest> callback) {
    try {
      final Long upload = request.getLong("upload", true, 409609);
      final String md5 = request.getString("md5", true, 443401);
      nexus.executor.execute(() -> {
        callback.success(new AttachmentFinishRequest(upload, md5));
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
