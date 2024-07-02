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

import java.util.ArrayList;

/** mark content that is static */
public class MarkStaticContent {
  public static boolean sweepAndMark(Element element) {
    boolean root = Rules.isRoot(element.tagName());
    if (!root) {
      if (Rules.isSpecialElement(element.tagName())) {
        return false;
      }
    }
    for (Attribute attribute : element.attributes()) {
      if (Rules.isSpecialAttribute(attribute.getKey())) {
        return false;
      }
    }
    boolean result = true;
    ArrayList<Element> toMark = new ArrayList<>(element.childNodeSize());
    for (Element child : element.children()) {
      if (sweepAndMark(child)) {
        toMark.add(child);
      } else {
        result = false;
      }
    }
    if (!result || root) {
      for (Element mark : toMark) {
        mark.attr("static:content", true);
      }
    }
    return result;
  }
  public static void mark(Document document) {
    for (Element element : document.getElementsByTag("template")) {
      sweepAndMark(element);
    }
    for (Element element : document.getElementsByTag("page")) {
      sweepAndMark(element);
    }
  }
}
