package org.adamalang.web.api;

import java.util.concurrent.Executor;
import org.adamalang.web.extern.Authenticator;
import org.adamalang.web.extern.SpacePolicyLocator;

class ConnectionNexus {
  public final Executor executor;
  public final Authenticator identityService;
  public final SpacePolicyLocator spaceService;

  public ConnectionNexus(Executor executor, Authenticator identityService, SpacePolicyLocator spaceService) {
    this.executor = executor;    this.identityService = identityService;
    this.spaceService = spaceService;
  }
}
