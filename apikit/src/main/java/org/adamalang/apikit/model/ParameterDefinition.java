/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.apikit.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class ParameterDefinition {
  public final String name;
  public final String camelName;
  public final Type type;
  public final boolean optional;
  public final Transform transform;
  public final Validator validator;
  public final String documentation;
  public final int errorCodeIfMissing;
  public final HashSet<String> skipTransformOnMethods;
  public final boolean logged;

  public ParameterDefinition(final String name, Type type, boolean optional, Transform transform, Validator validator, String documentation, int errorCodeIfMissing, final HashSet<String> skipTransformOnMethods, boolean logged) {
    this.name = name;
    this.camelName = Common.camelize(name, true);
    this.type = type;
    this.optional = optional;
    this.transform = transform;
    this.validator = validator;
    this.documentation = documentation;
    this.errorCodeIfMissing = errorCodeIfMissing;
    this.skipTransformOnMethods = skipTransformOnMethods;
    this.logged = logged;
  }

  public String invent() {
    if ("identity".equals(name)) {
      return "_identity";
    }
    if ("email".equals(name)) {
      return "\"x@x.com\"";
    }
    switch (type) {
      case JsonObject:
        return "Json.newJsonObject()";
      case Boolean:
        return "false";
      case String:
        return "\"xzya\"";
      case Integer:
        return "42";
      case Long:
        return "100L";
    }
    throw new NullPointerException();
  }

  public static Map<String, ParameterDefinition> buildMap(Document document) throws Exception {
    TreeMap<String, ParameterDefinition> parameters = new TreeMap<>();
    NodeList list = document.getElementsByTagName("parameter-definition");
    for (int k = 0; k < list.getLength(); k++) {
      Node node = list.item(k);
      if (node.getNodeType() != Node.ELEMENT_NODE) continue;
      Element element = (Element) node;
      String name = element.getAttribute("name");
      if (name == null) {
        throw new Exception("parameter-definition needs a name");
      }
      String rawType = element.getAttribute("type");
      if (rawType == null) {
        throw new Exception("parameter-definition needs a type");
      }
      String rawMissingErrorCode = element.getAttribute("missing-error");
      if (rawMissingErrorCode == null || "".equals(rawMissingErrorCode)) {
        rawMissingErrorCode = "0";
      }

      Type type = Type.of(rawType);
      if (type == null) {
        throw new Exception("parameter-definition's type must be valid");
      }
      boolean optional = "true".equals(element.getAttribute("optional"));
      boolean logged = "true".equals(element.getAttribute("logged"));

      String documentation = null;
      Transform transform = null;
      Validator validator = null;
      HashSet<String> skipTransforms = new HashSet<>();
      int errorCodeIfMissing = Integer.parseInt(rawMissingErrorCode);

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
          case "validate": {
            String service = childElement.getAttribute("service");
            if (service == null) {
              throw new Exception("validate needs a service");
            }
            validator = new Validator(service);
          }
          break;
          case "skip-transform": {
            String methodOn = childElement.getAttribute("on");
            if (methodOn == null || "".equals(methodOn)) {
              throw new Exception("skip-transform needs a on to identify a method");
            }
            skipTransforms.add(methodOn);
          }
          break;
          case "transform": {
            String service = childElement.getAttribute("service");
            if (service == null) {
              throw new Exception("transform needs a service");
            }
            String outputName = childElement.getAttribute("output-name");
            if (outputName == null) {
              throw new Exception("transform needs an output-name");
            }
            String outputJavaName = childElement.getAttribute("output-java-type");
            if (outputJavaName == null) {
              throw new Exception("transform needs an output-java-type");
            }
            String errorCodeOnFailureRaw = childElement.getAttribute("error-code");
            if (errorCodeOnFailureRaw == null) {
              throw new Exception("transform needs an error-code");
            }
            int errorCodeOnFailure = Integer.parseInt(errorCodeOnFailureRaw);
            transform = new Transform(name, type, service, outputName, outputJavaName, errorCodeOnFailure);
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
      ParameterDefinition definition = new ParameterDefinition(name, type, optional, transform, validator, documentation, errorCodeIfMissing, skipTransforms, logged);
      if (parameters.containsKey(name)) {
        throw new Exception("parameter already defined: " + name);
      }
      parameters.put(name, definition);
    }
    return parameters;
  }

  public Transform getTransform(String method) {
    if (skipTransformOnMethods.contains(method)) {
      return null;
    }
    return transform;
  }
}
