package org.adamalang.clikit.model;

import org.adamalang.clikit.exceptions.XMLFormatException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Map;

public class Argument {
    /** Represents an argument that is used by a command **/
    public final boolean optional;
    public final String name;
    public final String defaultValue;
    public final ArgumentDefinition definition;
    public final String camel;

    public Argument(String name, boolean optional, String defaultValue, ArgumentDefinition definition) {
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
