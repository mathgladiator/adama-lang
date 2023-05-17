package org.adamalang.clikit.model;

import org.adamalang.clikit.exceptions.XMLFormatException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
    public static String lJust(String string, int spacing) {
        return String.format("%-" + spacing + "s", string);
    }
    public static String camelize(String name) {
        String returnString = "";
        String[] splitString = name.split("-");
        for (String word : splitString) {
            returnString += capitalize(word);
        }
        return returnString;
    }
    public static String getDocumentation(Node itemNode, XMLFormatException givenException) throws Exception {

        Element item = (Element) itemNode;
        NodeList documentations = item.getElementsByTagName("documentation");
        Node docNode = Common.getFirstNode(documentations);
        if (docNode == null)
            givenException.addToExceptionStack(item.getTagName() + " element \"" + item.getAttribute("name")
            + "\" is missing documentation.");
        String documentation = docNode.getTextContent().trim();
        if (documentation.length() == 0) {
            givenException.addToExceptionStack((item.getTagName() + " element \"" + item.getAttribute("name")
                    + "\" documentation is empty."));
        }
        return documentation;
    }
    public static String getDocumentation(Node itemNode) throws Exception {

        Element item = (Element) itemNode;
        NodeList documentations = item.getElementsByTagName("documentation");
        Node docNode = Common.getFirstNode(documentations);
        if (docNode == null)
            throw new Exception(item.getTagName() + " element \"" + item.getAttribute("name")
                    + "\" is missing documentation.");
        String documentation = docNode.getTextContent().trim();
        if (documentation.length() == 0) {
            throw new Exception((item.getTagName() + " element \"" + item.getAttribute("name")
                    + "\" documentation is empty."));
        }
        return documentation;
    }

}
