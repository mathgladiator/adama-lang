/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.transforms.results;

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
    if (user.source == AuthenticatedUser.Source.Adama) {
      return user.id == owner;
    }
    return false;
  }

  public boolean canUserGeneratePrivateKey(AuthenticatedUser user) {
    if (user.source == AuthenticatedUser.Source.Adama) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserSetRole(AuthenticatedUser user) {
    if (user.source == AuthenticatedUser.Source.Adama) {
      return user.id == owner;
    }
    return false;
  }

  public boolean canUserSetPlan(AuthenticatedUser user) {
    if (user.source == AuthenticatedUser.Source.Adama) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserManageDomain(AuthenticatedUser user) {
    if (user.source == AuthenticatedUser.Source.Adama) {
      return user.id == owner;
    }
    return false;
  }

  public boolean canUserSetRxHTML(AuthenticatedUser user) {
    if (user.source == AuthenticatedUser.Source.Adama) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserGetRxHTML(AuthenticatedUser user) {
    if (user.source == AuthenticatedUser.Source.Adama) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserSeeReflection(AuthenticatedUser user) {
    if (user.source == AuthenticatedUser.Source.Adama) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserSeeKeyListing(AuthenticatedUser user) {
    if (user.source == AuthenticatedUser.Source.Adama) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserGetPlan(AuthenticatedUser user) {
    if (user.source == AuthenticatedUser.Source.Adama) {
      if (user.id == owner) {
        return true;
      }
      return developers.contains(user.id);
    }
    return false;
  }

  public boolean canUserGetBillingUsage(AuthenticatedUser user) {
    if (user.source == AuthenticatedUser.Source.Adama) {
      return user.id == owner;
    }
    return false;
  }
}
