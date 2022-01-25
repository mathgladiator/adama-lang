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
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.validators.ValidateKey;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/**  */
public class AttachmentStartRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final SpacePolicy policy;
  public final String key;
  public final String filename;
  public final String contentType;

  public AttachmentStartRequest(final String identity, final AuthenticatedUser who, final String space, final SpacePolicy policy, final String key, final String filename, final String contentType) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.key = key;
    this.filename = filename;
    this.contentType = contentType;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AttachmentStartRequest> callback) {
    try {
      final BulkLatch<AttachmentStartRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      ValidateSpace.validate(space);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String key = request.getString("key", true, 466947);
      ValidateKey.validate(key);
      final String filename = request.getString("filename", true, 470028);
      final String contentType = request.getString("content-type", true, 455691);
      _latch.with(() -> new AttachmentStartRequest(identity, who.get(), space, policy.get(), key, filename, contentType));
      nexus.identityService.execute(nexus.session, identity, who);
      nexus.spaceService.execute(nexus.session, space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
