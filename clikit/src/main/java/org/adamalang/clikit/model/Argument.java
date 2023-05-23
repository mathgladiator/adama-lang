package org.adamalang.clikit.model;

import org.adamalang.clikit.exceptions.XMLFormatException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Map;

public class Argument {
    public boolean optional;
    public String name;
    public String shortName;
    public String defaultValue = "";
    public ArgDefinition definition;

    public Argument(String name, boolean optional, String defaultValue, ArgDefinition definition) {
        this.optional = optional;
        this.name = name;
        this.defaultValue = defaultValue;
        this.definition = definition;
    }
    public Argument(String name, boolean optional) {
        this.optional = optional;
        this.name = name;
    }


    public Argument(String name) {
        this.optional = false;
        this.name = name;
    }


    public static Argument[] createArgumentList(NodeList nodeList, XMLFormatException givenException, Map<String, ArgDefinition> arguments) {
        ArrayList<Argument> argumentArray = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node argNode = nodeList.item(i);
            ArgDefinition argDef = null;
            Element argElem = (Element) argNode;
            String argName = argElem.getAttribute("name");

            if (argName == null || "".equals(argName.trim()))
                givenException.addToExceptionStack("Argument name can not be empty.");
            if (!arguments.containsKey(argName))
                givenException.addToExceptionStack("Argument \"" + argName + "\" is not a defined argument");
            else
                argDef = arguments.get(argName);
            boolean optional = false;
            if (argElem.hasAttribute("default")) {
                optional = true;
            }

            String defaultValue = argElem.getAttribute("default");
            String headerType = argElem.getAttribute("type");

            Argument argument;

            if (argDef != null)
                argument = new Argument(argName, optional, defaultValue, argDef);
            else
                argument = new Argument(argName, optional);
            argumentArray.add(argument);

        }
        return argumentArray.toArray(new Argument[argumentArray.size()]);
    }
}
