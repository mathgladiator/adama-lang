package org.adamalang.apikit.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;
import java.util.TreeMap;

public class ParameterDefinition {
    public final String name;
    public final Type type;
    public final boolean optional;
    public final Lookup lookup;
    public final String documentation;
    public final int errorCodeIfMissing;

    public ParameterDefinition(final String name, Type type, boolean optional, Lookup lookup, String documentation, int errorCodeIfMissing) {
        this.name = name;
        this.type = type;
        this.optional = optional;
        this.lookup = lookup;
        this.documentation = documentation;
        this.errorCodeIfMissing = errorCodeIfMissing;
    }

    public static Map<String, ParameterDefinition> buildMap(Document document) throws Exception {
        TreeMap<String, ParameterDefinition> parameters = new TreeMap<>();
        NodeList list = document.getElementsByTagName("parameter-definition");
        for (int k = 0; k < list.getLength(); k++) {
            Node node = list.item(k);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element element = (Element) node;
            String name = element.getAttribute("name");
            System.err.println("start:" + name);
            if (name == null) {
                throw new Exception("parameter-definition needs a name");
            }
            String rawType = element.getAttribute("type");
            if (rawType == null) {
                throw new Exception("parameter-definition needs a type");
            }
            Type type = Type.of(rawType);
            if (type == null) {
                throw new Exception("parameter-definition's type must be valid");
            }
            boolean optional = "true".equals(element.getAttribute("optional"));

            String documentation = null;
            Lookup lookup = null;
            int errorCodeIfMissing = 0;

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
                    case "lookup": {
                        String service = childElement.getAttribute("service");
                        if (service == null) {
                            throw new Exception("lookup needs a service");
                        }
                        String outputName = childElement.getAttribute("output-name");
                        if (outputName == null) {
                            throw new Exception("lookup needs an output-name");
                        }
                        String outputJavaName = childElement.getAttribute("output-java-type");
                        if (outputJavaName == null) {
                            throw new Exception("lookup needs an output-java-type");
                        }
                        String errorCodeOnFailureRaw = childElement.getAttribute("error-code");
                        if (errorCodeOnFailureRaw == null) {
                            throw new Exception("lookup needs an error-code");
                        }
                        int errorCodeOnFailure = Integer.parseInt(errorCodeOnFailureRaw);
                        lookup = new Lookup(name, type, service, outputName, outputJavaName, errorCodeOnFailure);
                    }
                    break;
                    case "error": {
                        String errorCodeOnMissingRaw = childElement.getAttribute("code");
                        if (errorCodeOnMissingRaw == null) {
                            throw new Exception("error needs a code");
                        }
                        errorCodeIfMissing = Integer.parseInt(errorCodeOnMissingRaw);
                    }
                    break;
                }
            }
            if (documentation == null) {
                throw new Exception("parameter has no documentation");
            }
            if (errorCodeIfMissing == 0 && !optional) {
                throw new Exception("non-optional parameter is missing non-zero error code:" + name);
            }
            ParameterDefinition definition = new ParameterDefinition(name, type, optional, lookup, documentation, errorCodeIfMissing);
            if (parameters.containsKey(name)) {
                throw new Exception("parameter already defined: " + name);
            }
            System.err.println("define:" + name);
            parameters.put(name, definition);
        }
        return parameters;
    }

}
