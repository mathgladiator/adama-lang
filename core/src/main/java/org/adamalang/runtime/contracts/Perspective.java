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
package org.adamalang.runtime.contracts;

/**
 * a perspective is a consumer of a stream of delta updates from the vantage point of a viewer on a
 * device.
 */
public interface Perspective {
  /** a dead perspective, not useful except for people that don't care about the data */
  Perspective DEAD = new Perspective() {
    @Override
    public void data(String data) {
    }

    @Override
    public void disconnect() {
    }
  };

  /** new data for the user */
  void data(String data);

  /** the server disconnected the stream */
  void disconnect();
}
