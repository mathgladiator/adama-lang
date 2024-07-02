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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Defines an argument that can be used by CLI commands **/
public class ArgumentDefinition {

    public final String type;
    public final String documentation;
    public final String shortField;
    public ArgumentDefinition(String type, String docs, String shortField) {
        type = type.toLowerCase(Locale.ROOT);
        this.documentation = docs.trim() // LAME
            .replaceAll(Pattern.quote("\""), Matcher.quoteReplacement("\\\"")) //
            .replaceAll(Pattern.quote("\n"), Matcher.quoteReplacement("\\n")) //
            .replaceAll(Pattern.quote("\r"), Matcher.quoteReplacement(""));
        this.type = type;
        this.shortField = shortField;
    }

    public static HashMap<String, ArgumentDefinition> createMap(Document doc, XMLFormatException givenException) throws Exception {
        HashMap<String, ArgumentDefinition> returnMap = new HashMap<>();
        NodeList nodeList = doc.getElementsByTagName("arg-definition");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node argNode = nodeList.item(i);
            String filePos = "line " + argNode.getUserData("lineNumber") + " column " + argNode.getUserData("colNumber");
            Element elementNode = (Element) argNode;
            String docs = Common.getDocumentation(elementNode, givenException);
            String type = elementNode.getAttribute("type");
            //TODO: Validate if it is one of the types soon, assume always has type for now
            String elementName = elementNode.getAttribute("name");
            if ("".equals(elementName.trim())) {
                givenException.addToExceptionStack("The 'arg-definition' node at " + filePos + " is missing name attribute");
            }
            String shortField = elementNode.getAttribute("short");
            returnMap.put(elementName, new ArgumentDefinition(type, docs, shortField));
        }
        return returnMap;
    }
}
