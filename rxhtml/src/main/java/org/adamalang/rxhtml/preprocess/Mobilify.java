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

import org.adamalang.rxhtml.ProductionMode;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/** handle the mobile: prefix attributes */
public class Mobilify {
  private static final String MOBILE_PREFIX = "mobile:";
  private static int MOBILE_PREFIX_LENGTH = MOBILE_PREFIX.length();

  private static void walk(Document document, BiConsumer<Element, HashMap<String, String>> consumer) {
    for (Element element : document.getAllElements()) {
      HashMap<String, String> mobile = new HashMap<>();
      ArrayList<String> remove = new ArrayList<>();
      for (Attribute attr : element.attributes()) {
        if (attr.getKey().startsWith(MOBILE_PREFIX)) {
          if (attr.hasDeclaredValue()) {
            mobile.put(attr.getKey().substring(MOBILE_PREFIX_LENGTH), attr.getValue());
          } else {
            mobile.put(attr.getKey().substring(MOBILE_PREFIX_LENGTH), null);
          }
          remove.add(attr.getKey());
        }
      }
      if (mobile.size() > 0) {
        for (String toRemove : remove) {
          element.removeAttr(toRemove);
        }
        consumer.accept(element, mobile);
      }
    }
  }

  public static void go(Document document, ProductionMode mode) {
    if (mode == ProductionMode.MobileApp) {
      walk(document, (e, m) -> {
        for (Map.Entry<String, String> entry : m.entrySet()) {
          if (entry.getValue() == null) {
            e.attr(entry.getKey(), true);
          } else {
            e.attr(entry.getKey(), entry.getValue());
          }

        }
      });
    } else {
      walk(document, (e, m) -> {});
    }
  }
}
