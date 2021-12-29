/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.apikit.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;
import java.util.TreeMap;

public class FieldDefinition {
    public final String name;
    public final String camelName;
    public final Type type;
    public final boolean optional;
    public final String documentation;

    public FieldDefinition(final String name, Type type, boolean optional, String documentation) {
        this.name = name;
        this.camelName = Common.camelize(name, true);
        this.type = type;
        this.optional = optional;
        this.documentation = documentation;
    }

    public static Map<String, FieldDefinition> buildMap(Document document) throws Exception {
        TreeMap<String, FieldDefinition> fields = new TreeMap<>();
        NodeList list = document.getElementsByTagName("field-definition");
        for (int k = 0; k < list.getLength(); k++) {
            Node node = list.item(k);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element element = (Element) node;
            String name = element.getAttribute("name");
            if (name == null) {
                throw new Exception("field-definition needs a name");
            }
            String rawType = element.getAttribute("type");
            if (rawType == null) {
                throw new Exception("field-definition needs a type");
            }
            Type type = Type.of(rawType);
            if (type == null) {
                throw new Exception("field-definition's type must be valid");
            }
            boolean optional = "true".equals(element.getAttribute("optional"));
            String documentation = null;

            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node childNode = children.item(j);
                if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element childElement = (Element) childNode;
                switch (childElement.getTagName()) {
                    case "documentation": {
                        documentation = childElement.getTextContent();
                    }
                    break;
                }
            }
            if (documentation == null) {
                throw new Exception("field has no documentation");
            }
            FieldDefinition definition = new FieldDefinition(name, type, optional, documentation);
            if (fields.containsKey(name)) {
                throw new Exception("field already defined: " + name);
            }
            fields.put(name, definition);
        }
        return fields;
    }

}
