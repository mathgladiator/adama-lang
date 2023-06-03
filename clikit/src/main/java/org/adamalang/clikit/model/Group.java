package org.adamalang.clikit.model;

import org.adamalang.clikit.exceptions.XMLFormatException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Map;

public class Group {
    /** A group is a representation of a collection of commands in the XML **/
    public final String documentation;
    public final String name;
    public final String capName;
    public final Command[] commandList;

    public Group(String name,String documentation, Command[] commandList) {
        this.name = name;
        this.capName = Common.camelize(name);
        this.documentation = documentation;
        this.commandList = commandList;
    }

    public static Group[] createGroupList(NodeList nodeList, XMLFormatException givenException, Map<String, ArgumentDefinition> arguments) throws Exception {
        ArrayList<Group> groupArray = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node groupNode = nodeList.item(i);
            String filePos = "line " + groupNode.getUserData("lineNumber") + " column " + groupNode.getUserData("colNumber");
            Element groupElem = (Element) groupNode;
            String groupName = groupElem.getAttribute("name");
            if (groupName.trim().length() == 0)
                givenException.addToExceptionStack("The 'group' node at " + filePos + " is missing name attribute");
            Command[] commandList = Command.createCommandList(groupElem.getElementsByTagName("command"), givenException, arguments);
            String groupDocumentation = Common.getDocumentation(groupElem, givenException);
            Group group = new Group(groupName, groupDocumentation ,commandList);
            groupArray.add(group);
        }
        return groupArray.toArray(new Group[groupArray.size()]);
    }
}
