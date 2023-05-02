/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
