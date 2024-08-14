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
package org.adamalang.rxhtml;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.rxhtml.template.Task;

import java.util.ArrayList;
import java.util.HashMap;

/** data from the RxHTML compiler to the tooling */
public class Diagnostics {
  public final HashMap<String, Integer> cssFreq;
  public final ArrayList<Task> tasks;
  public final ObjectNode viewSchema;
  public final int javascriptSize;

  public Diagnostics(HashMap<String, Integer> cssFreq, ArrayList<Task> tasks, ObjectNode viewSchema, int javascriptSize) {
    this.cssFreq = cssFreq;
    this.tasks = tasks;
    this.viewSchema = viewSchema;
    this.javascriptSize = javascriptSize;
  }
}
