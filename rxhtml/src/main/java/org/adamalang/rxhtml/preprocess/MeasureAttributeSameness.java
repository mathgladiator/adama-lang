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
package org.adamalang.rxhtml.preprocess;

import org.adamalang.rxhtml.template.Rules;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;

/** measure attributes that are the same */
public class MeasureAttributeSameness {
  private static void account(HashMap<String, HashMap<String, Integer>> nameToValueCounts, Element element) {
    if (!Rules.isSpecialElement(element.tagName())) {
      for (Attribute attribute : element.attributes()) {
        if (!Rules.isSpecialAttribute(attribute.getKey())) {
          HashMap<String, Integer> countsByValue = nameToValueCounts.get(attribute.getKey());
          if (countsByValue == null) {
            countsByValue = new HashMap<>();
            nameToValueCounts.put(attribute.getKey(), countsByValue);
          }
          Integer prior = countsByValue.get(attribute.getValue());
          if (prior == null) {
            prior = 0;
          }
          countsByValue.put(attribute.getValue(), prior + 1);
        }
      }
    }
    for (Element child : element.children()) {
      account(nameToValueCounts, child);
    }
  }

  public static HashMap<String, HashMap<String, Integer>> measure(Document document) {
    HashMap<String, HashMap<String, Integer>> nameToValueCounts = new HashMap<>();
    for (Element element : document.getElementsByTag("template")) {
      account(nameToValueCounts, element);
    }
    for (Element element : document.getElementsByTag("page")) {
      account(nameToValueCounts, element);
    }
    return nameToValueCounts;
  }
}
