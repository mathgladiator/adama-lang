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
package org.adamalang.runtime.ops;

import org.adamalang.runtime.contracts.DocumentMonitor;

/** a monitor which does nothing */
public class SilentDocumentMonitor implements DocumentMonitor {
  @Override
  public void assertFailureAt(final int startLine, final int startPosition, final int endLine, final int endLinePosition, final int total, final int failures) {
  }

  @Override
  public void goodwillFailureAt(final int startLine, final int startPosition, final int endLine, final int endLinePosition) {
  }

  @Override
  public void pop(final long time, final boolean exception) {
  }

  @Override
  public void push(final String label) {
  }
}
