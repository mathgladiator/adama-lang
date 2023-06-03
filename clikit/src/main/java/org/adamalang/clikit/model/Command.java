package org.adamalang.clikit.model;

import org.adamalang.clikit.exceptions.XMLFormatException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Map;

public class Command {
    public final Argument[] argList;
    public final String output;
    public final String capName;
    public final String name;
    public final String documentation;
    public final boolean danger;
    public final String camel;

    public Command(String name, String documentation, String output, boolean danger, Argument[] argList) {
        /** A command is a representation of an instruction for communctation with Adama's backend **/
        this.name = name;
        this.camel = Common.camelize(name, true);
        this.capName = Common.camelize(name);
        this.documentation = documentation;
        this.argList = argList;
        this.output = output;
        this.danger = danger;
    }

    public static Command[] createCommandList(NodeList nodeList, XMLFormatException givenException, Map<String, ArgumentDefinition> arguments) throws Exception{
        ArrayList<Command> commandArray = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node commandNode = nodeList.item(i);
            String filePos = "line " + commandNode.getUserData("lineNumber") + " column " + commandNode.getUserData("colNumber");
            Element commandElem = (Element) commandNode;
            String commandName = commandElem.getAttribute("name");
            if (commandName == null || commandName.trim().isEmpty())
                givenException.addToExceptionStack("The 'command' node at " + filePos + " is missing name attribute");
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

            Command command = new Command(commandName, groupDocumentation, outputArg, danger, argumentList);
            commandArray.add(command);
        }
        return commandArray.toArray(new Command[commandArray.size()]);
    }

    public static Command[] createCommandList(NodeList nodeList, XMLFormatException givenException, Map<String, ArgumentDefinition> arguments, String parent) throws Exception{
        ArrayList<Command> commandArray = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node commandNode = nodeList.item(i);
            String filePos = "line " + commandNode.getUserData("lineNumber") + " column " + commandNode.getUserData("colNumber");
            Element commandElem = (Element) commandNode;
            // Will always have parent
            Node parentNode = commandElem.getParentNode();
            if (!parentNode.getNodeName().equals(parent)) {
                continue;
            }

            String commandName = commandElem.getAttribute("name");
            if (commandName == null || commandName.trim().isEmpty())
                givenException.addToExceptionStack("The 'command' node at " + filePos + " is missing name attribute");
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

            Command command = new Command(commandName, groupDocumentation, outputArg, danger, argumentList);
            commandArray.add(command);
        }
        return commandArray.toArray(new Command[commandArray.size()]);
    }
}
