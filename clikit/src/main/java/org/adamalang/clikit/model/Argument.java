/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
