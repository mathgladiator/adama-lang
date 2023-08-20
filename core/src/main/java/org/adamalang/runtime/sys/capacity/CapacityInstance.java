/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CapacityInstance instance = (CapacityInstance) o;
    return override == instance.override && Objects.equals(space, instance.space) && Objects.equals(region, instance.region) && Objects.equals(machine, instance.machine);
  }

  @Override
  public int hashCode() {
    return Objects.hash(space, region, machine, override);
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
