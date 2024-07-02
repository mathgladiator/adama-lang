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
package org.adamalang.clikit.model;

import org.adamalang.clikit.exceptions.XMLFormatException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.PrimitiveIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A utility class to support the model converting from XML to Java with functions like camelize **/
public class Common {
    public static Node getFirstNode(NodeList nodeList) {
        if (nodeList.getLength() == 0)
            return null;
        return nodeList.item(0);
    }

    public static String capitalize(String name) {
        if (!name.isEmpty())
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        return "";
    }

    public static String camelize(String name) {
        String returnString = "";
        String[] splitString = name.split("-");
        for (String word : splitString) {
            returnString += capitalize(word);
        }
        return returnString;
    }

    public static String escape(String s) {
        // Escapes strings for use in java file text
        StringBuilder sb = new StringBuilder();
        PrimitiveIterator.OfInt it = s.codePoints().iterator();
        while (it.hasNext()) {
            int cp = it.next();
            switch (cp) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\n':
                    sb.append(" ");
                    break;
                default:
                    sb.append(Character.toChars(cp));
                    break;
            }
        }
        return sb.toString();
    }

    public static String camelize(String name, boolean firstLower) {
        if (firstLower) {
            String[] parts = name.split("-");
            for(int k = 1; k < parts.length; k++) {
                parts[k] = capitalize(parts[k]);
            }
            return String.join("", parts);
        } else {
            return camelize(name);
        }
    }

    public static String getDocumentation(Node itemNode, XMLFormatException givenException) throws Exception {
        Element item = (Element) itemNode;
        NodeList documentations = item.getElementsByTagName("documentation");
        Node docNode = Common.getFirstNode(documentations);
        String filePos = "line " + itemNode.getUserData("lineNumber") + " column " + itemNode.getUserData("colNumber");
        String documentation = null;
        if (docNode == null)
            givenException.addToExceptionStack("The " + item.getTagName() + " element \"" + item.getAttribute("name")
                    + "\""  + " at " + filePos + " " + "is missing documentation");
        else
            documentation = docNode.getTextContent().trim();


        if (documentation != null && documentation.length() == 0) {
            givenException.addToExceptionStack("The " + item.getTagName() + " element \"" + item.getAttribute("name")
                    + "\""  + " at " + filePos + " " + "has empty documentation");
        }
        return documentation;
    }


}
