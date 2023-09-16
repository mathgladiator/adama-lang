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
