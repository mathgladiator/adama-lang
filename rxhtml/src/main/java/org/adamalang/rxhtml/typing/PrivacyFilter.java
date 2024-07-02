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
package org.adamalang.rxhtml.typing;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.TreeSet;

/** scope of allowed privacy policies */
public class PrivacyFilter {
  public final TreeSet<String> allowed;

  public PrivacyFilter(String... allow) {
    this.allowed = new TreeSet<>();
    for (String x : allow) {
      if (x.length() > 0) {
        allowed.add(x);
      }
    }
  }

  @Override
  public String toString() {
    ArrayList<String> parts = new ArrayList<>();
    parts.addAll(allowed);
    return String.join(", ", parts);
  }

  public boolean visible(JsonNode privacy) {
    if (privacy == null) {
      return false;
    }
    if (privacy.isTextual()) {
      String fieldPolicy = privacy.textValue();
      if ("private".equals(fieldPolicy)) {
        return false;
      }
      if ("public".equals(fieldPolicy) || "bubble".equals(fieldPolicy)) {
        return true;
      }
      return false;
    } else if (privacy.isArray()) {
      for (int k = 0; k < privacy.size(); k++) {
        if (!allowed.contains(privacy.get(k).textValue())) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }
}
