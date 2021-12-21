package org.adamalang.transforms.results;

import org.adamalang.mysql.frontend.Spaces;

import java.util.Set;

public class SpacePolicy {
    public final int id;
    public final int owner;
    public final Set<Integer> developers;

    public SpacePolicy(Spaces.Space space) {
        this.id = space.id;
        this.owner = space.owner;
        this.developers = space.developers;
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
}
