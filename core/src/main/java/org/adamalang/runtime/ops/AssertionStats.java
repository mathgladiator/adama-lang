/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.ops;

/** what happened during a run */
public class AssertionStats {
  public final int failures;
  public final int total;

  public AssertionStats(final int total, final int failures) {
    this.total = total;
    this.failures = failures;
  }
}
