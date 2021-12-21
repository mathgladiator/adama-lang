package org.adamalang.transforms.results;

import org.adamalang.mysql.frontend.Spaces;

public class SpacePolicy {
    public final int id;
    public final int owner;

    public SpacePolicy(Spaces.Space space) {
        this.id = space.id;
        this.owner = space.owner;
    }

    public boolean canUserSetRole(int user) {
        return user == owner;
    }

    public boolean canUserSetPlan(int user) {
        return true;
    }
}
