package org.adamalang.clikit.model;

import org.adamalang.clikit.exceptions.XMLFormatException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.HashMap;

/** Defines an argument that can be used by CLI commands **/
public class ArgumentDefinition {

    public final String type;
    public final String documentation;
    public final String shortField;
    public ArgumentDefinition(String type, String docs, String shortField) {
        this.documentation = docs;
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
            String docs = Common.getDocumentation(elementNode);
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
