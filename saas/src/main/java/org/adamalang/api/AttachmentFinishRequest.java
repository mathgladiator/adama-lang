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

/**  */
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
