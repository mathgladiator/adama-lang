/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.data;

/** a region bound machine */
public class CapacityInstance {
  public final int id;
  public final String space;
  public final String region;
  public final String machine;
  public final boolean override;

  public CapacityInstance(int id, String space, String region, String machine, boolean override) {
    this.id = id;
    this.space = space;
    this.region = region;
    this.machine = machine;
    this.override = override;
  }
}
