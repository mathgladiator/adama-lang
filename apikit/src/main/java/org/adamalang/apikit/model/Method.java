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
    public final String documentation;
    public final Responder responder;
    public final String handler;
    public final String create;
    public final String findBy;
    public final int errorCantFindBy;
    public final boolean destroy;
    public final boolean callOnDisconnect;

    public Method(String name, ParameterDefinition[] parameters, String documentation, Responder responder, String handler, String create, String findBy, int errorCantFindBy, boolean destroy, boolean callOnDisconnect) {
        this.name = name;
        this.camelName = Common.camelize(name);
        this.parameters = parameters;
        this.documentation = documentation;
        this.responder = responder;
        this.handler = handler != null && handler.length() == 0 ? null : handler;
        this.create = create != null && create.length() == 0 ? null : create;
        this.findBy = findBy != null && findBy.length() == 0 ? null : findBy;
        this.errorCantFindBy = errorCantFindBy;
        this.destroy = destroy;
        this.callOnDisconnect = callOnDisconnect;
    }

    public TreeSet<String> imports() {
        TreeSet<String> set = new TreeSet<>();
        set.add("org.adamalang.runtime.exceptions.ErrorCodeException");
        set.add("org.adamalang.web.io.*");
        set.add("org.adamalang.runtime.contracts.Callback");
        for (ParameterDefinition parameter : parameters) {
            if (parameter.transform != null) {
                if (parameter.transform.outputJavaType.indexOf('.') > 0) {
                    set.add(parameter.transform.outputJavaType);
                }
            }
            if (parameter.validator != null) {
                set.add(parameter.validator.service);
            }
            if (parameter.type == Type.JsonObject) {
                set.add("com.fasterxml.jackson.databind.node.ObjectNode");
            }
        }
        return set;
    }

    public static Method[] methodsOf(Document document, Map<String, ParameterDefinition> parameters, Map<String, Responder> responders) throws Exception {
        NodeList list = document.getElementsByTagName("method");
        ArrayList<Method> methodsArrayList = new ArrayList<>();
        for (int k = 0; k < list.getLength(); k++) {
            Node node = list.item(k);
            Element element = (Element) node;
            String name = element.getAttribute("name");
            if (name == null || "".equals(name)) {
                throw new Exception("missing name value: " + node);
            }

            String responderValue = element.getAttribute("responder");
            if (responderValue == null || "".equals(responderValue)) {
                throw new Exception("missing responder value: " + node + "/" + name);
            }
            Responder responder = responders.get(responderValue);
            if (responder == null) {
                throw new Exception("responder not found:" + responderValue);
            }

            String createValue = element.getAttribute("create");
            String findByValue = element.getAttribute("find-by");
            String errorCantFindByText = element.getAttribute("error-find-by");
            int errorCantFindBy = 0;
            if (findByValue != null && findByValue.length() > 0) {
                errorCantFindBy = Integer.parseInt(errorCantFindByText);
            }
            String handlerValue = element.getAttribute("handler");
            if (handlerValue != null && handlerValue.length() > 0) {
                handlerValue = Common.camelize(handlerValue);
            } else {
                handlerValue = "Root";
            }
            boolean destroy = "true".equals(element.getAttribute("destroy"));
            boolean callOnDisconnect = "true".equals(element.getAttribute("call-on-disconnect"));

            String documentation = null;
            ArrayList<ParameterDefinition> parametersArrayList = new ArrayList<>();
            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node childItem = children.item(j);
                if (childItem.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) childItem;
                    if ("parameter".equals(childElement.getTagName())) {
                        String parameterName = childElement.getAttribute("name");
                        if (parameterName == null) {
                            throw new Exception("parameters must have name");
                        }
                        ParameterDefinition parameter = parameters.get(parameterName);
                        if (parameter == null) {
                            throw new Exception("unable to find parameter: " + parameterName);
                        }
                        parametersArrayList.add(parameter);
                    } else if ("documentation".equals(childElement.getTagName())) {
                        documentation = childElement.getTextContent();
                    } else {
                        throw new Exception("unrecognized tag under method:" + childElement);
                    }
                }
            }
            if (documentation == null) {
                throw new Exception("method has no documentation");
            }
            methodsArrayList.add(new Method(name, parametersArrayList.toArray(new ParameterDefinition[parametersArrayList.size()]), documentation, responder, handlerValue, createValue, findByValue, errorCantFindBy, destroy, callOnDisconnect));
        }
        return methodsArrayList.toArray(new Method[methodsArrayList.size()]);
    }
}
