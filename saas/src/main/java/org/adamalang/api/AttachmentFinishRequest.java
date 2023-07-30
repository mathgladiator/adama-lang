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

/** Finishing uploading the attachment upload. */
public class AttachmentFinishRequest {
  public final Long upload;

  public AttachmentFinishRequest(final Long upload) {
    this.upload = upload;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<AttachmentFinishRequest> callback) {
    try {
      final Long upload = request.getLong("upload", true, 409609);
      nexus.executor.execute(new NamedRunnable("attachmentfinish-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new AttachmentFinishRequest(upload));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("attachmentfinish-error") {
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
