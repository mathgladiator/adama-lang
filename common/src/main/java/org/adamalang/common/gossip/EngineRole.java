/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.gossip;

import java.util.Random;

public enum EngineRole {
  SuperNode(15, 4), Node(50, 5);

  public final int waitBasis;
  public final int waitRounds;

  EngineRole(int waitBasis, int waitRounds) {
    this.waitBasis = waitBasis;
    this.waitRounds = waitRounds;
  }

  public static int computeWait(Random jitter, EngineRole role) {
    int wait = role.waitBasis;
    for (int k = 0; k < role.waitRounds; k++) {
      wait += jitter.nextInt(role.waitBasis);
    }
    return wait;
  }
}
