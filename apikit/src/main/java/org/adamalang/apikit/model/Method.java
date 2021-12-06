package org.adamalang.apikit.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

public class Method {
    public final String name;
    public final String camelName;
    public final ParameterDefinition[] parameters;

    public Method(String name, ParameterDefinition[] parameters) {
        this.name = name;
        this.camelName = Common.camelize(name);
        this.parameters = parameters;
    }

    public TreeSet<String> imports() {
        TreeSet<String> set = new TreeSet<>();
        set.add("java.util.concurrent.Executor");
        set.add("org.adamalang.runtime.exceptions.ErrorCodeException");
        set.add("org.adamalang.web.io.*");
        set.add("org.adamalang.runtime.contracts.Callback");
        for (ParameterDefinition parameter : parameters) {
            if (parameter.lookup != null) {
                set.add(parameter.lookup.service);
                if (parameter.lookup.outputJavaType.indexOf('.') > 0) {
                    set.add(parameter.lookup.outputJavaType);
                }
            }
            if (parameter.type == Type.JsonObject) {
                set.add("com.fasterxml.jackson.databind.node.ObjectNode");
            }
        }
        return set;
    }

    public static Method[] methodsOf(Document document, Map<String, ParameterDefinition> parameters) throws Exception {
        NodeList list = document.getElementsByTagName("method");
        ArrayList<Method> methodsArrayList = new ArrayList<>();
        for (int k = 0; k < list.getLength(); k++) {
            Node node = list.item(k);
            Node nameNode = node.getAttributes().getNamedItem("name");
            if (nameNode == null) {
                throw new Exception("missing name: " + node);
            }
            String name = nameNode.getNodeValue();
            if (name == null) {
                throw new Exception("missing name value: " + node);
            }
            ArrayList<ParameterDefinition> parametersArrayList = new ArrayList<>();
            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node childItem = children.item(j);
                if (childItem.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) childItem;
                    if ("parameter".equals(element.getTagName())) {
                        String parameterName = element.getAttribute("name");
                        if (parameterName == null) {
                            throw new Exception("parameters must have name");
                        }
                        ParameterDefinition parameter = parameters.get(parameterName);
                        if (parameter == null) {
                            throw new Exception("unable to find parameter: " + parameterName);
                        }
                        parametersArrayList.add(parameter);
                    } else {
                        throw new Exception("unrecognized tag under method:" + element);
                    }
                }
            }
            methodsArrayList.add(new Method(name, parametersArrayList.toArray(new ParameterDefinition[parametersArrayList.size()])));
        }
        return methodsArrayList.toArray(new Method[methodsArrayList.size()]);
    }
}
