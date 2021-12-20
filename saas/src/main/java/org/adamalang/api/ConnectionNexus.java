package org.adamalang.api;

import java.util.concurrent.Executor;
import org.adamalang.transforms.Authenticator;
import org.adamalang.transforms.SpacePolicyLocator;
import org.adamalang.transforms.UserIdResolver;

public class ConnectionNexus {
  public final Executor executor;
  public final UserIdResolver emailService;
  public final Authenticator identityService;
  public final SpacePolicyLocator spaceService;

  public ConnectionNexus(Executor executor, UserIdResolver emailService, Authenticator identityService, SpacePolicyLocator spaceService) {
    this.executor = executor;    this.emailService = emailService;
    this.identityService = identityService;
    this.spaceService = spaceService;
  }
}
