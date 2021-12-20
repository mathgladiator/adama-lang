package org.adamalang.api;

import java.util.concurrent.Executor;
import org.adamalang.transforms.Authenticator;
import org.adamalang.transforms.SpacePolicyLocator;

public class ConnectionNexus {
  public final Executor executor;
  public final Authenticator identityService;
  public final SpacePolicyLocator spaceService;

  public ConnectionNexus(Executor executor, Authenticator identityService, SpacePolicyLocator spaceService) {
    this.executor = executor;    this.identityService = identityService;
    this.spaceService = spaceService;
  }
}
