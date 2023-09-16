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
package org.adamalang.common.template.tree;

import com.fasterxml.jackson.databind.JsonNode;
import org.adamalang.common.template.Settings;
import org.jsoup.nodes.Entities;

/** pull the variable into the document */
public class TVariable implements T {
  public final String variable;
  public final boolean unescape;

  public TVariable(String variable, boolean unescape) {
    this.variable = variable;
    this.unescape = unescape;
  }

  @Override
  public void render(Settings settings, JsonNode node, StringBuilder output) {
    JsonNode fetched = node.get(variable);
    if (fetched != null && fetched.isTextual()) {
      if (settings.html && !unescape) {
        output.append(Entities.escape(fetched.textValue()));
      } else {
        output.append(fetched.textValue());
      }
    }
  }
}
