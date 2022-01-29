/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.deployments.data;

public class Deployment {
  public final String space;
  public final String hash;
  public final String plan;
  public final String target;

  public Deployment(String space, String hash, String plan, String target) {
    this.space = space;
    this.hash = hash;
    this.plan = plan;
    this.target = target;
  }
}
