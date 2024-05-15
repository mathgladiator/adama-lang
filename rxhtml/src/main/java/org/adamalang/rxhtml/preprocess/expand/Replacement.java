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
package org.adamalang.rxhtml.preprocess.expand;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.List;

/** missing feature in JSoup */
public class Replacement {
  public static void replace(Element elementToReplace, List<Node> replaceWith) {
    Element parent = elementToReplace.parent();
    int k = 0;
    int found = -1;
    for (Node child : parent.childNodes()) {
      if (child == elementToReplace) {
        found = k;
      }
      k++;
    }
    if (found >= 0) {
      parent.insertChildren(found, replaceWith);
    }
    elementToReplace.remove();
  }
}
