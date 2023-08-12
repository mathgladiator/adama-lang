/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.contracts.DomainWithPolicyResolver;
import org.adamalang.contracts.SpacePolicyLocator;
import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.contracts.UserIdResolver;
import org.adamalang.web.io.JsonLogger;;

public class GlobalConnectionNexus {
  public final JsonLogger logger;
  public final GlobalApiMetrics metrics;
  public final SimpleExecutor executor;
  public final DomainWithPolicyResolver domainService;
  public final UserIdResolver emailService;
  public final PerSessionAuthenticator identityService;
  public final SpacePolicyLocator spaceService;

  public GlobalConnectionNexus(JsonLogger logger, GlobalApiMetrics metrics, SimpleExecutor executor, DomainWithPolicyResolver domainService, UserIdResolver emailService, PerSessionAuthenticator identityService, SpacePolicyLocator spaceService) {
    this.logger = logger;
    this.metrics = metrics;
    this.executor = executor;
    this.domainService = domainService;
    this.emailService = emailService;
    this.identityService = identityService;
    this.spaceService = spaceService;
  }
}
