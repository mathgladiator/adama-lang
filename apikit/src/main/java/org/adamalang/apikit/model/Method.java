/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.apikit.model;

import org.adamalang.apikit.DocumentHelper;
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
  public final String camelName2;
  public final ParameterDefinition[] parameters;
  public final String documentation;
  public final Responder responder;
  public final String handler;
  public final String create;
  public final String createCamel;
  public final String findBy;
  public final int errorCantFindBy;
  public final boolean destroy;
  public final boolean callOnDisconnect;
  public final boolean devbox;
  public final String scope;
  public final boolean internal;
  public final boolean checkPolicy;
  public final int policyErrorCode;
  public final String defaultPolicyBehavior;
  public final boolean genService;

  public Method(String name, ParameterDefinition[] parameters, String documentation, Responder responder, String handler, String create, String findBy, int errorCantFindBy, boolean destroy, boolean callOnDisconnect, boolean devbox, String scope, boolean internal, boolean checkPolicy, int policyErrorCode, String defaultPolicyBehavior, boolean genService) {
    this.name = name;
    this.camelName = Common.camelize(name);
    this.camelName2 = Common.camelize(name, true);
    this.parameters = parameters;
    this.documentation = documentation;
    this.responder = responder;
    this.handler = handler != null && handler.length() == 0 ? null : handler;
    this.create = create != null && create.length() == 0 ? null : create;
    this.findBy = findBy != null && findBy.length() == 0 ? null : findBy;
    this.createCamel = Common.camelize(create);
    this.errorCantFindBy = errorCantFindBy;
    this.destroy = destroy;
    this.callOnDisconnect = callOnDisconnect;
    this.devbox = devbox;
    this.scope = scope;
    this.internal = internal;
    this.checkPolicy = checkPolicy;
    this.policyErrorCode = policyErrorCode;
    this.defaultPolicyBehavior = defaultPolicyBehavior;
    this.genService = genService;
  }

  public static Method[] methodsOf(Document document, Map<String, ParameterDefinition> parameters, Map<String, Responder> responders) throws Exception {
    NodeList list = document.getElementsByTagName("method");
    ArrayList<Method> methodsArrayList = new ArrayList<>();
    for (int k = 0; k < list.getLength(); k++) {
      Node node = list.item(k);
      Element element = (Element) node;
      String name = DocumentHelper.attribute(element, "name");
      String responderValue = DocumentHelper.attribute(element, "responder");
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
      } else {
        findByValue = null;
      }
      String handlerValue = element.getAttribute("handler");
      String scope = element.getAttribute("scope");
      if (handlerValue != null && handlerValue.length() > 0) {
        handlerValue = Common.camelize(handlerValue);
      } else {
        handlerValue = "Root" + Common.camelize(scope);
      }
      boolean destroy = "true".equals(element.getAttribute("destroy"));
      boolean internal = "true".equals(element.getAttribute("internal"));
      boolean devbox = "true".equals(element.getAttribute("devbox"));
      boolean genService = "true".equals(element.getAttribute("gen-service"));
      boolean callOnDisconnect = "true".equals(element.getAttribute("call-on-disconnect"));

      boolean checkPolicy = "true".equals(element.getAttribute("policy"));
      int policyErrorCode = 0;
      String defaultPolicyBehavior = "Owner";
      if (checkPolicy) {
        try {
          policyErrorCode = Integer.parseInt(element.getAttribute("policy-failure-code"));
        } catch (Exception ex) {
          throw new Exception(element.toString() + " is missing 'policy-failure-code'");
        }
      }
      if (element.hasAttribute("policy-default")) {
        defaultPolicyBehavior = element.getAttribute("policy-default");
      }

      boolean shouldDoAPolicyCheck = false;
      boolean inferedPolicy = findByValue != null;
      String documentation = null;
      ArrayList<ParameterDefinition> parametersArrayList = new ArrayList<>();
      NodeList children = node.getChildNodes();
      for (int j = 0; j < children.getLength(); j++) {
        Node childItem = children.item(j);
        if (childItem.getNodeType() == Node.ELEMENT_NODE) {
          Element childElement = (Element) childItem;
          if ("parameter".equals(childElement.getTagName())) {
            String parameterName = DocumentHelper.attribute(childElement, "name");
            ParameterDefinition parameter = parameters.get(parameterName);
            if (parameter == null) {
              throw new Exception("unable to find parameter: " + parameterName);
            }
            if (parameter.requiresPolicyCheck) {
              shouldDoAPolicyCheck = true;
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
      System.out.println("\u001b[36mAPI:\u001b[0m" + name + (inferedPolicy && !internal ? " \u001b[35m(POLICY INFER)\u001b[0m" : "") + (shouldDoAPolicyCheck && !internal ? " \u001b[36m(POLICY CHECKED)\u001b[0m" : "") + (internal ? " \u001b[31m(INTERNAL)\u001b[0m" : ""));
      methodsArrayList.add(new Method(name, parametersArrayList.toArray(new ParameterDefinition[parametersArrayList.size()]), documentation, responder, handlerValue, createValue, findByValue, errorCantFindBy, destroy, callOnDisconnect, devbox, scope, internal, checkPolicy, policyErrorCode, defaultPolicyBehavior, genService));
      boolean hasManualPolicy =  "true".equals(element.getAttribute("policy-manual"));
      boolean hasDocumentPolicy = "true".equals(element.getAttribute("policy-via-document"));
      if (shouldDoAPolicyCheck && !(hasManualPolicy || checkPolicy || hasDocumentPolicy || internal)) {
        throw new Exception("the method '" + name + "' has no policy aspect defined, yet is using a protected resource");
      }
    }
    return methodsArrayList.toArray(new Method[methodsArrayList.size()]);
  }

  public TreeSet<String> imports(String sessionImport) {
    TreeSet<String> set = new TreeSet<>();
    set.add("org.adamalang.common.ErrorCodeException");
    set.add("org.adamalang.web.io.*");
    set.add("org.adamalang.common.Callback");
    set.add(sessionImport);
    set.add("org.adamalang.common.NamedRunnable");
    set.add("com.fasterxml.jackson.databind.node.ObjectNode");

    for (ParameterDefinition parameter : parameters) {
      Transform transform = parameter.getTransform(this.name);
      if (transform != null) {
        if (transform.outputJavaType.indexOf('.') > 0) {
          set.add(transform.outputJavaType);
        }
      }
      if (parameter.validator != null) {
        set.add(parameter.validator.service);
      }
      if (parameter.type == Type.JsonObject) {
        set.add("com.fasterxml.jackson.databind.node.ObjectNode");
      }
      if (parameter.type == Type.JsonObjectOrArray) {
        set.add("com.fasterxml.jackson.databind.JsonNode");
      }
    }
    return set;
  }
}
