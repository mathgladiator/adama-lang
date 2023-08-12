/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.contracts.data;

import org.adamalang.mysql.data.SpaceInfo;

import java.util.Set;

/** the policy backing a space */
public class SpacePolicy {
  public final int id;
  public final int owner;
  private final Set<Integer> developers;

  public SpacePolicy(SpaceInfo space) {
    this.id = space.id;
    this.owner = space.owner;
    this.developers = space.developers;
  }

  public boolean canUserDeleteSpace(AuthenticatedUser user) {
    if (user.isAdamaDeveloper) {
      return user.id == owner;
    }
    return false;
  }

  public boolean canUserGeneratePrivateKey(AuthenticatedUser user) {
    if (user.isAdamaDeveloper) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserSetRole(AuthenticatedUser user) {
    if (user.isAdamaDeveloper) {
      return user.id == owner;
    }
    return false;
  }

  public boolean canUserSetPlan(AuthenticatedUser user) {
    if (user.isAdamaDeveloper) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserManageDomain(AuthenticatedUser user) {
    if (user.isAdamaDeveloper) {
      return user.id == owner;
    }
    return false;
  }

  public boolean canUserListDeveloper(AuthenticatedUser user) {
    if (user.isAdamaDeveloper) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserSetRxHTML(AuthenticatedUser user) {
    if (user.isAdamaDeveloper) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserGetRxHTML(AuthenticatedUser user) {
    if (user.isAdamaDeveloper) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserSeeReflection(AuthenticatedUser user) {
    if (user.isAdamaDeveloper) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserSeeKeyListing(AuthenticatedUser user) {
    if (user.isAdamaDeveloper) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserGetPlan(AuthenticatedUser user) {
    if (user.isAdamaDeveloper) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserGetBillingUsage(AuthenticatedUser user) {
    if (user.isAdamaDeveloper) {
      return user.id == owner;
    }
    return false;
  }
}
