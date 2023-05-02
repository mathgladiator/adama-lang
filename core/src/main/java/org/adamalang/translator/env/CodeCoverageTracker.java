/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.env;

import org.adamalang.translator.tree.common.DocumentPosition;

import java.util.ArrayList;

/**
 * This tracks code coverage within blocks. Each position in the document is associated to an
 * integer, and integers get registered as code executes. This also provides the ability to do some
 * limited remote debugging by being able to step through each statement.
 */
public class CodeCoverageTracker {
  public final ArrayList<DocumentPosition> positions;

  public CodeCoverageTracker() {
    positions = new ArrayList<>();
  }

  /**
   * track the given position and associate to an integer for tracking purposes
   * @param position the position to register
   * @return the index to track
   */
  public int register(final DocumentPosition position) {
    final var idx = positions.size();
    positions.add(position);
    return idx;
  }
}
