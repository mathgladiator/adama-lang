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

import org.adamalang.common.SimpleExecutor;
import org.adamalang.connection.Session;
import org.adamalang.transforms.Authenticator;
import org.adamalang.transforms.SpacePolicyLocator;
import org.adamalang.transforms.UserIdResolver;
import org.adamalang.web.io.JsonLogger;;

public class ConnectionNexus {
  public final JsonLogger logger;
  public final ApiMetrics metrics;
  public final SimpleExecutor executor;
  public final Session session;
  public final UserIdResolver emailService;
  public final Authenticator identityService;
  public final SpacePolicyLocator spaceService;

  public ConnectionNexus(Session session, JsonLogger logger, ApiMetrics metrics, SimpleExecutor executor, UserIdResolver emailService, Authenticator identityService, SpacePolicyLocator spaceService) {
    this.logger = logger;
    this.metrics = metrics;
    this.executor = executor;
    this.session = session;
    this.emailService = emailService;
    this.identityService = identityService;
    this.spaceService = spaceService;
  }
}
