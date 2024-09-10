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
package org.adamalang.runtime.data;

import org.adamalang.common.ErrorCodeException;

/** Observe data changes at a lower level */
public interface DataObserver {
  /** The machine that is observing */
  String machine();

  /** an initial payload representing a snapshot */
  void start(String snapshot);

  /** a rollback happened */
  void change(String delta);

  /** a failure occurred */
  void failure(ErrorCodeException exception);
}
