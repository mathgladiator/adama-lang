/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.contracts.data;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.mysql.data.SpaceInfo;

import java.util.Set;

/** the policy backing a space */
public class SpacePolicy {
  public final int id;
  public final int owner;
  private final Set<Integer> developers;
  public ObjectNode policy;

  public SpacePolicy(SpaceInfo space) {
    this.id = space.id;
    this.owner = space.owner;
    this.developers = space.developers;
    this.policy = Json.parseJsonObject(space.policy);
  }

  public boolean isOwner(AuthenticatedUser user) {
    if (user.isAdamaDeveloper) {
      return user.id == owner;
    }
    return false;
  }

  public boolean checkPolicy(String method, DefaultPolicyBehavior defaultPolicyBehavior, AuthenticatedUser user) {
    if (user.isAdamaDeveloper && user.id == owner) {
      return true;
    }
    // TODO LOOK UP IN THE POLICY BY METHOD

    return false;
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

  public boolean canGetMetrics(AuthenticatedUser user) {
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
}
