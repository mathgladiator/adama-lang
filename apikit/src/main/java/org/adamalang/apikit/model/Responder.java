package org.adamalang.apikit.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Responder {
    public final String name;
    public final String camelName;
    public final boolean stream;
    public final FieldDefinition[] fields;

    public Responder(String name, boolean stream, FieldDefinition[] fields) {
        this.name = name;
        this.camelName = Common.camelize(name);
        this.stream = stream;
        this.fields = fields;
    }

    public TreeSet<String> imports() {
        TreeSet<String> set = new TreeSet<>();
        set.add("org.adamalang.runtime.exceptions.ErrorCodeException");
        set.add("org.adamalang.web.io.*");
        set.add("org.adamalang.runtime.contracts.Callback");
        set.add("com.fasterxml.jackson.databind.node.ObjectNode");
        set.add("com.fasterxml.jackson.databind.json.JsonMapper");
        return set;
    }

    public static Map<String, Responder> respondersOf(Document document, Map<String, FieldDefinition> fields) throws Exception {
        TreeMap<String, Responder> responders = new TreeMap<>();
        NodeList list = document.getElementsByTagName("responder");
        for (int k = 0; k < list.getLength(); k++) {
            Node node = list.item(k);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element element = (Element) node;
            String name = element.getAttribute("name");
            if (name == null) {
                throw new Exception("responder needs a name");
            }
            boolean stream = "true".equals(element.getAttribute("stream"));
            ArrayList<FieldDefinition> fieldsForThisResponder = new ArrayList<>();
            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node childNode = children.item(j);
                if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element childElement = (Element) childNode;
                switch (childElement.getTagName()) {
                    case "field": {
                        String fieldName = childElement.getAttribute("name");
                        if (fieldName == null) {
                            throw new Exception("field must have a name");
                        }
                        FieldDefinition fieldToUse = fields.get(fieldName);
                        if (fieldToUse == null) {
                            throw new Exception("field must be valid");
                        }
                        fieldsForThisResponder.add(fieldToUse);
                    }
                    break;
                }
            }
            Responder responder = new Responder(name, stream, fieldsForThisResponder.toArray(new FieldDefinition[fieldsForThisResponder.size()]));
            responders.put(responder.name, responder);
        }
        return responders;
    }
}
