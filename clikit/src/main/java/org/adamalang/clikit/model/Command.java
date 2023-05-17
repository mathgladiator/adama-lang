package org.adamalang.clikit.model;

import org.adamalang.clikit.exceptions.XMLFormatException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Map;

public class Command {
    public Argument[] argList;
    public String output;
    public String capName;
    public String name;
    public String method;
    public String documentation;
    public String endpoint;
    public boolean danger;

    public Command(String name, String documentation, String method, String output, String endpoint, boolean danger, Argument[] argList) {
        this.name = name;
        this.capName = Common.camelize(name);
        this.documentation = documentation;
        this.endpoint = endpoint;
        this.method = method;
        this.argList = argList;
        this.output = output;
        this.danger = danger;
    }

    public static Command[] createCommandList(NodeList nodeList, XMLFormatException givenException, Map<String, ArgDefinition> arguments) throws Exception{
        ArrayList<Command> commandArray = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node commandNode = nodeList.item(i);
            Element commandElem = (Element) commandNode;
            String commandName = commandElem.getAttribute("name");
            if (commandName == null || commandName.trim().isEmpty())
                givenException.addToExceptionStack("Command name \"" + commandName + "\"can not be empty.");
            Argument[] argumentList = Argument.createArgumentList(commandElem.getElementsByTagName("arg"), givenException, arguments);
            boolean danger = commandElem.getAttribute("warn").equals("") ? false : true;
            String groupDocumentation = Common.getDocumentation(commandElem, givenException);
            String methodType = commandElem.getAttribute("method");
            if ( methodType == null || "".equals(methodType.trim()))
                methodType = "self";
            String outputArg = "";
            if (commandElem.hasAttribute("output")) {
                outputArg = commandElem.getAttribute("output");
            } else {
                outputArg = null;
            }
            String endpoint = commandElem.getAttribute("endpoint");

            Command command = new Command(commandName, groupDocumentation, methodType, outputArg, endpoint, danger, argumentList);
            commandArray.add(command);
        }
        return commandArray.toArray(new Command[commandArray.size()]);
    }
}
