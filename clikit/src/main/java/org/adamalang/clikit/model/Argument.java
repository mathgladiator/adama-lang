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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/** Represents an argument that is used by a command **/
public class Argument {

    public final boolean optional;
    public final String name;
    public final String defaultValue;
    public final ArgumentDefinition definition;
    public final String camel;

    public Argument(String name, boolean optional, String defaultValue, ArgumentDefinition definition) {
        name = name.toLowerCase(Locale.ROOT);
        this.optional = optional;
        this.name = name;
        this.camel = Common.camelize(name, true);
        this.defaultValue = defaultValue;
        this.definition = definition;
    }

    public static Argument[] createArgumentList(NodeList nodeList, XMLFormatException givenException, Map<String, ArgumentDefinition> arguments) {
        ArrayList<Argument> argumentArray = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node argNode = nodeList.item(i);
            ArgumentDefinition argDef = null;
            Element argElem = (Element) argNode;
            String filePos = "line " + argNode.getUserData("lineNumber") + " column " + argNode.getUserData("colNumber");
            String argName = argElem.getAttribute("name");

            if (argName == null || "".equals(argName.trim()))
                givenException.addToExceptionStack("The 'arg' node at " + filePos + " is missing name attribute");
            if (!arguments.containsKey(argName))
                givenException.addToExceptionStack("The 'arg' node named \"" + argName + "\" at " + filePos + " is not a defined argument");
            else
                argDef = arguments.get(argName);
            boolean optional = false;
            if (argElem.hasAttribute("default")) {
                optional = true;
            }
            String defaultValue = argElem.getAttribute("default");
            String headerType = argElem.getAttribute("type");
            Argument argument = new Argument(argName, optional, defaultValue, argDef);
            argumentArray.add(argument);

        }
        return argumentArray.toArray(new Argument[argumentArray.size()]);
    }
}
