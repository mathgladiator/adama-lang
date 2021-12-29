/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.transforms.results;

import org.adamalang.mysql.frontend.Spaces;

import java.util.Set;

public class SpacePolicy {
    public final int id;
    public final int owner;
    private final Set<Integer> developers;

    public SpacePolicy(Spaces.Space space) {
        this.id = space.id;
        this.owner = space.owner;
        this.developers = space.developers;
    }

    public boolean canUserChangeOwner(AuthenticatedUser user) {
        if (user.source == AuthenticatedUser.Source.Adama) {
            return user.id == owner;
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

    public boolean canUserGetPlan(AuthenticatedUser user) {
        if (user.source == AuthenticatedUser.Source.Adama) {
            if (user.id == owner) {
                return true;
            }
            return developers.contains(user.id);
        }
        return false;
    }
}
