package org.adamalang.rxhtml;

import org.w3c.dom.*;

public class Template {
  private final StringBuilder sb = new StringBuilder();
  private int autoVar;
  private int tabAt;
  private String tabCache;

  private Template() {
    autoVar = 0;
    tabAt = 0;
    tabCache = "";
  }

  private String newVar() {
    String v = "_" + autoVar;
    autoVar++;
    return v;
  }

  private void rebuildTabCache() {
    StringBuilder tabCacheBuilder = new StringBuilder();
    for (int k = 0; k < tabAt; k++) {
      tabCacheBuilder.append("  ");
    }
    tabCache = tabCacheBuilder.toString();
  }

  private Template tabUp() {
    tabAt++;
    rebuildTabCache();
    return this;
  }

  private Template tabDown() {
    tabAt--;
    rebuildTabCache();
    return this;
  }

  private Template tab() {
    sb.append(tabCache);
    return this;
  }

  private Template append(String s) {
    sb.append(s);
    return this;
  }

  private Template newline() {
    sb.append("\n");
    return this;
  }

  private void writeRoot(Element element) {
    String rootVar = newVar();
    append("_templates['").append(element.getAttribute("name")).append("'] = function(tree) {").newline().tabUp();
    tab().append("var d = {};").newline();
    tab().append("var ").append(rootVar).append(" = document.createElement('div');").newline();
    writeChildren(element, true, rootVar);
    tab().append("tree.onTreeChange(d);").newline();
    tab().append("return ").append(rootVar).append(";").newline();
    append("}").newline();
  }

  private void writeElement(Element element, String parentVariable) {
    String eVar = newVar();
    tab().append("var ").append(eVar).append(" = document.createElement('").append(element.getTagName()).append("');").newline();
    // TODO: attributes
    NamedNodeMap attrs = element.getAttributes();
    for (int k = 0; k < attrs.getLength(); k++) {
      Node attrNode = attrs.item(k);
      Attr attr = (Attr) attrNode;
      // attr.getName()
      // attr.getValue()
      // TODO: get the mapping of xml name to
      // TODO: LOOK FOR GUARDS, and then USE THEM
      tab().append(eVar).append(".").append(attr.getName()).append(" = '").append(attr.getValue()).append("';").newline();
    }
    writeChildren(element, element.getChildNodes().getLength() == 1, eVar);
    tab().append(parentVariable).append(".append(").append(eVar).append(");").newline();
  }

  private void writeText(Text text, String parentVariable) {
    tab().append(parentVariable).append(".append(document.createTextNode('").append(text.getTextContent()).append("'));").newline();
  }

  private void writeIterate(String current, Element template, String name, String parentVariable) {
    String setVar = newVar();
    tab().append("d.").append(name).append(" = {").tabUp().newline();
    tab().append("'+': function(").append(setVar).append(") {").tabUp().newline();
    tab().append("var d = {};").newline();
    writeElement(template, parentVariable);
    tab().append("return d;").newline();
    tabDown().tab().append("},").newline();
    tab().append("'-': function(").append(setVar).append(") {").tabUp().newline();
    tabDown().tab().append("},").newline();
    tab().append("'^': function(").append(setVar).append(") {").tabUp().newline();
    tabDown().tab().append("}").newline();
    tabDown().tab().append("}").newline();
  };

  private void writeLookup(String current, String name, String parentVariable) {
    String eVar = newVar();
    tab().append("var ").append(eVar).append(" = document.createTextNode(").append(current != null ? (current + "." + name) : "''").append(");").newline();
    if (current != null) {
      tab().append(eVar).append(".nodeValue = x.value;").newline().tabDown();
    }
    // PULL from the current
    tab().append("d.").append(name).append(" = function(x) {").newline().tabUp();
    tab().append(eVar).append(".nodeValue = x.value;").newline().tabDown();
    // ALL SORTS OF FORMATTING FUN
    tab().append("};").newline();
    tab().append(parentVariable).append(".append(").append(eVar).append(");").newline();
  }

  private Element getSingleChild(Element element) {
    NodeList children = element.getChildNodes();
    if (children.getLength() != 1) {
      throw new UnsupportedOperationException("z");
    }
    Node child = children.item(0);
    if (child.getNodeType() != Node.ELEMENT_NODE) {
      throw new UnsupportedOperationException();
    }
    return (Element) child;
  }

  private void writeChildren(Element element, boolean singleParent, String parentVariable) {
    NodeList list = element.getChildNodes();
    if (list != null && list.getLength() > 0) {
      for (int k = 0; k < list.getLength(); k++) {
        Node node = list.item(k);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element child = (Element) node;
          switch (child.getTagName()) {
            case "scope": {
              String into = child.getAttribute("into");
              String oldDelta = newVar();
              tab().append("var ").append(oldDelta).append(" = d;").newline();
              tab().append("d = {};").newline();
              writeChildren(child, singleParent, parentVariable);
              tab().append(oldDelta).append(".").append(into).append("d").newline();
              tab().append("d = ").append(oldDelta).append(";").newline();
              break;
            }
            case "iterate": {
              if (!singleParent) {
                throw new UnsupportedOperationException("Yo");
              }
              writeIterate(null,  getSingleChild(child), child.getAttribute("name"), parentVariable);
            }
            case "lookup": {

              writeLookup(null, child.getAttribute("name"), parentVariable);
              break;
            }
            default: {
              writeElement(child, parentVariable);
              break;
            }
          }
        } else if (node.getNodeType() == Node.TEXT_NODE) {
          writeText((Text) node, parentVariable);
        // } else if (node.getNodeType() == Node.ENTITY_NODE) {
          // ((Entity) node).
        } else {
          throw new UnsupportedOperationException("not sure how to handle node type:" + node.getNodeType() + "/" + node);
        }
      }
    }
  }

  @Override
  public String toString() {
    return sb.toString();
  }

  public static String convertTemplateToJavaScript(Element element) {
    Template template = new Template();
    template.writeRoot(element);
    return template.toString();
  }
}
