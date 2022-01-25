/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.api;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.connection.Session;
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
