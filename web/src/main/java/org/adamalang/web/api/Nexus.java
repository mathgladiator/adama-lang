package org.adamalang.web.api;

import org.adamalang.web.extern.Authenticator;
import org.adamalang.web.extern.SpacePolicyLocator;

class Nexus {
  public final Authenticator identityService;
  public final SpacePolicyLocator spaceService;

  public Nexus(Authenticator identityService, SpacePolicyLocator spaceService) {
    this.identityService = identityService;
    this.spaceService = spaceService;
  }
}
