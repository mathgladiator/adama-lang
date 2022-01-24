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

import java.util.concurrent.Executor;
import org.adamalang.transforms.Authenticator;
import org.adamalang.transforms.SpacePolicyLocator;
import org.adamalang.transforms.UserIdResolver;

public class ConnectionNexus {
  public final ApiMetrics metrics;
  public final Executor executor;
  public final UserIdResolver emailService;
  public final Authenticator identityService;
  public final SpacePolicyLocator spaceService;

  public ConnectionNexus(ApiMetrics metrics, Executor executor, UserIdResolver emailService, Authenticator identityService, SpacePolicyLocator spaceService) {
    this.metrics = metrics;
    this.executor = executor;
    this.emailService = emailService;
    this.identityService = identityService;
    this.spaceService = spaceService;
  }
}
