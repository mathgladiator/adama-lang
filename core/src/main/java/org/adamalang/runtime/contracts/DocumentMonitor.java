/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.contracts;

/** a document can be monitored */
public interface DocumentMonitor {
  /** an assertion failure happened */
  void assertFailureAt(int startLine, int startPosition, int endLine, int endLinePosition, int total, int failures);

  /** a goodwill failure happened (i.e an infinite loop) */
  void goodwillFailureAt(int startLine, int startPosition, int endLine, int endLinePosition);

  /** a function has returned, and here is the timing for it */
  void pop(long time, boolean exception);

  /** push a function on the call stack to measure timing */
  void push(String label);
}
