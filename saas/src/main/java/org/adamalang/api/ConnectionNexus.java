/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.connection.Session;
import org.adamalang.transforms.DomainResolver;
import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.transforms.SpacePolicyLocator;
import org.adamalang.transforms.UserIdResolver;
import org.adamalang.web.io.JsonLogger;;

public class ConnectionNexus {
  public final JsonLogger logger;
  public final ApiMetrics metrics;
  public final SimpleExecutor executor;
  public final DomainResolver domainService;
  public final UserIdResolver emailService;
  public final PerSessionAuthenticator identityService;
  public final SpacePolicyLocator spaceService;

  public ConnectionNexus(JsonLogger logger, ApiMetrics metrics, SimpleExecutor executor, DomainResolver domainService, UserIdResolver emailService, PerSessionAuthenticator identityService, SpacePolicyLocator spaceService) {
    this.logger = logger;
    this.metrics = metrics;
    this.executor = executor;
    this.domainService = domainService;
    this.emailService = emailService;
    this.identityService = identityService;
    this.spaceService = spaceService;
  }
}
