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

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

public class StaticConfig {
  public final String name;
  public final String push;
  public final String order;
  public final String code;
  public final String children;
  public final String parent;
  public final String id;

  public StaticConfig(Element element) {
    String _name = null;
    String _push = "path";
    String _order = "ordering";
    String _code = "code";
    String _children = "children";
    String _parent = "parent";
    String _id = "id";
    if (element != null) {
      for (Attribute attribute : element.attributes()) {
        if (attribute.hasDeclaredValue() || attribute.getValue() == null) {
          continue;
        }
        String val = attribute.getValue().trim();
        switch (attribute.getKey()) {
          case "push":
            _push = val;
            break;
          case "name":
            _name = val;
            break;
          case "order":
            _order = val;
            break;
          case "code":
            _code = val;
            break;
          case "children":
            _children = val;
            break;
          case "parent":
            _parent = val;
            break;
          case "id":
            _id = val;
            break;
        }
      }
    }
    this.name = _name;
    this.push = _push;
    this.order = _order;
    this.code = _code;
    this.children = _children;
    this.parent = _parent;
    this.id = _id;
  }
}
