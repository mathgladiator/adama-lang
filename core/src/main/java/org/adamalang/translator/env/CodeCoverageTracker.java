/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.env;

import java.util.ArrayList;
import org.adamalang.translator.tree.common.DocumentPosition;

/** This tracks code coverage within blocks. Each position in the document is
 * associated to an integer, and integers get registered as code executes. This
 * also provides the ability to do some limited remote debugging by being able
 * to step through each statement. */
public class CodeCoverageTracker {
  public final ArrayList<DocumentPosition> positions;

  public CodeCoverageTracker() {
    positions = new ArrayList<>();
  }

  /** track the given position and associate to an integer for tracking purposes
   *
   * @param position the position to register
   * @return the index to track */
  public int register(final DocumentPosition position) {
    final var idx = positions.size();
    positions.add(position);
    return idx;
  }
}
