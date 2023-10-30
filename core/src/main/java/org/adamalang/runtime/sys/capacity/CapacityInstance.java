/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.sys.capacity;

import java.util.Objects;

/** a region bound machine */
public class CapacityInstance implements Comparable<CapacityInstance> {
  public final String space;
  public final String region;
  public final String machine;
  public final boolean override;

  public CapacityInstance(String space, String region, String machine, boolean override) {
    this.space = space;
    this.region = region;
    this.machine = machine;
    this.override = override;
  }

  @Override
  public int hashCode() {
    return Objects.hash(space, region, machine, override);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CapacityInstance instance = (CapacityInstance) o;
    return override == instance.override && Objects.equals(space, instance.space) && Objects.equals(region, instance.region) && Objects.equals(machine, instance.machine);
  }

  @Override
  public int compareTo(CapacityInstance o) {
    int delta = region.compareTo(o.region);
    if (delta == 0) {
      delta = machine.compareTo(o.machine);
      if (delta == 0) {
        delta = space.compareTo(o.space);
        if (delta == 0) {
          delta = Boolean.compare(override, o.override);
        }
      }
    }
    return delta;
  }
}
