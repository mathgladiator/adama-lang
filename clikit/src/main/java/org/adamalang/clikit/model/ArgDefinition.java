package org.adamalang.clikit.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

public class ArgDefinition {
    public String type;
    public String documentation;
    public String shortField;
    public ArgDefinition(String type, String docs, String shortField) {
        this.documentation = docs;
        this.type = type;
        this.shortField = shortField;
    }
    public static HashMap<String, ArgDefinition> createMap(Document doc) throws Exception{
        HashMap<String, ArgDefinition> returnMap = new HashMap<>();
        NodeList nodeList = doc.getElementsByTagName("arg-definition");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node argNode = nodeList.item(i);
            if (argNode.getNodeType() != Node.ELEMENT_NODE)
                throw new Exception("placeholder");
            Element elementNode = (Element) argNode;
            String docs = Common.getDocumentation(elementNode);
            String type = elementNode.getAttribute("type");
            //TODO: Validate if it is one of the types soon, assume always has type for now
            String elementName = elementNode.getAttribute("name");
            if ("".equals(elementName.trim())) {
                throw new Exception("Empty name attribute");
            }
            String shortField = elementNode.getAttribute("short");
            returnMap.put(elementName, new ArgDefinition(type, docs, shortField));
        }
        return returnMap;
    }
}
