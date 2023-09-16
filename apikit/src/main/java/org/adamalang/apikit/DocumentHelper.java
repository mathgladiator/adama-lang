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
package org.adamalang.apikit;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DocumentHelper {

  public static Element first(NodeList list, String name) throws Exception {
    if (list == null || list.getLength() == 0) {
      throw new Exception("The list '"+name+"' is not present");
    }
    Node itemNode = list.item(0);
    if (itemNode instanceof Element) {
      return (Element) itemNode;
    }
    throw new Exception("The first item '"+name+"' is not a non-null element");
  }

  public static String attribute(Element element, String name) throws Exception {
    String value = element.getAttribute(name);
    if (value == null) {
      throw new Exception("Value '" + name + "' is not value");
    }
    value = value.trim();
    if ("".equals(value)) {
      throw new Exception("Value '" + name + "' is not value");
    }
    return value;
  }
}
