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
package org.adamalang.runtime.sys.capacity;

import org.adamalang.common.Callback;

import java.util.List;

/** an interface for managing capacity */
public interface CapacityOverseer {

  /** list all the capacity for a space in the world */
  void listAllSpace(String space, Callback<List<CapacityInstance>> callback);

  /** list all the capacity for a space within a region */
  void listWithinRegion(String space, String region, Callback<List<CapacityInstance>> callback);

  /** list all the spaces bound to a machine within a region */
  void listAllOnMachine(String region, String machine, Callback<List<CapacityInstance>> callback);

  /** add capacity for a space to a host within a region */
  void add(String space, String region, String machine, Callback<Void> callback);

  /** remove capacity for a space from a host and region */
  void remove(String space, String region, String machine, Callback<Void> callback);

  /** nuke all capacity for a space */
  void nuke(String space, Callback<Void> callback);

  /** pick a new host for a space within a region (that is stable) */
  void pickStableHostForSpace(String space, String region, Callback<String> callback);

  /** pick a new host for a space that hasn't been allocated */
  void pickNewHostForSpace(String space, String region, Callback<String> callback);
}
