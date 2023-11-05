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
package org.adamalang.system.contracts;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.sys.ServiceHeatEstimator;

import java.util.ArrayList;

/** configuration values for the service */
public interface JsonConfig {
  public String get_string(String field, String defaultValue);

  public int get_int(String field, int defaultValue);

  public ServiceHeatEstimator.HeatVector get_heat(String suffix, int cpu_m, int messages, int mem_mb, int connections);

  public ObjectNode get_or_create_child(String field);

  public ArrayList<String> get_str_list(String field);

  public ObjectNode read();
}
