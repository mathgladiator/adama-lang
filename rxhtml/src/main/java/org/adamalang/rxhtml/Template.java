package org.adamalang.rxhtml;

import org.w3c.dom.*;

public class Template {
  private final StringBuilder sb = new StringBuilder();
  private int tabAt;
  private String tabCache;
  private final VariablePool pool;

  private Template() {
    tabAt = 0;
    tabCache = "";
    pool = new VariablePool();
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
    String rootVar = pool.ask();
    append("_templates['").append(element.getAttribute("name")).append("'] = function(_tree, $) {").newline().tabUp();
    tab().append("var _ = {};").newline();
    tab().append("var ").append(rootVar).append(" = $.e('div');").newline();
    writeChildren("_tree.tree", element, true, rootVar);
    tab().append("_tree.onTreeChange(_);").newline();
    tab().append("return ").append(rootVar).append(";").newline();
    append("}").newline();
  }

  private String writeElement(String current, Element element, String parentVariable, boolean returnVariable) {
    String eVar = pool.ask();
    tab().append("var ").append(eVar).append(" = $.e('").append(element.getTagName()).append("');").newline();
    // TODO: attributes
    NamedNodeMap attrs = element.getAttributes();
    for (int k = 0; k < attrs.getLength(); k++) {
      Node attrNode = attrs.item(k);
      Attr attr = (Attr) attrNode;
      // if attribute value is prefixed by a $, then do a lookup (yay)
      // attr.getName()
      // attr.getValue()
      // TODO: get the mapping of xml name to
      // TODO: LOOK FOR GUARDS, and then USE THEM; need a language
      tab().append(eVar).append(".").append(attr.getName()).append(" = '").append(attr.getValue()).append("';").newline();
    }
    writeChildren(current, element, element.getChildNodes().getLength() == 1, eVar);
    tab().append(parentVariable).append(".append(").append(eVar).append(");").newline();
    if (returnVariable) {
      return eVar;
    } else {
      pool.give(eVar);
      return null;
    }
  }

  private void writeText(Text text, String parentVariable) {
    if (!text.getTextContent().trim().equalsIgnoreCase("")) {
      tab().append(parentVariable).append(".append($.t('").append(text.getTextContent()).append("'));").newline();
    }
  }

  private void writeIterate(String current, Element template, String name, String parentVariable) {
    String setVar = pool.ask();
    String gidVar = pool.ask();
    tab().append("var ").append(gidVar).append(" = $.g();").newline();
    tab().append("_.").append(name).append(" = {").tabUp().newline();
    tab().append("'+': function(").append(setVar).append(") {").tabUp().newline();
    tab().append("var _ = {};").newline();
    String appendElement = writeElement(setVar + ".value", template, parentVariable, true);
    tab().append(appendElement).append("._k = ").append(setVar).append(".key;").newline();
    tab().append(setVar).append(".value['").append(setVar).append("_' + ").append(gidVar).append("] = ").append(appendElement).append(";").newline();
    pool.give(appendElement);
    tab().append("return _;").newline();
    tabDown().tab().append("},").newline();
    tab().append("'-': function(").append(setVar).append(") {").tabUp().newline();
    tab().append("$.n(").append(setVar).append(".before['").append(setVar).append("_' + ").append(gidVar).append("]);").newline();
    tabDown().tab().append("},").newline();
    tab().append("'^': function(").append(setVar).append(") {").tabUp().newline();
    tab().append("$.r(").append(parentVariable).append(", ").append(setVar).append(");").newline();
    tabDown().tab().append("}").newline();
    tabDown().tab().append("}").newline();
    pool.give(setVar);
    pool.give(gidVar);
  };

  private void writeLookup(String current, String name, String transform, String parentVariable) {
    String eVar = pool.ask();
    tab().append("{").tabUp().newline();
    tab().append("var ").append(eVar).append(" = $.t(").append(current != null ? (current + "." + name) : "''").append(");").newline();
    String getValueNow = current + "." + name;
    String pullValue = "x.value";
    if ("ntclient.agent".equalsIgnoreCase(transform)) {
      getValueNow = getValueNow + ".agent";
      pullValue = pullValue + ".agent";
    }
    // MORE TRANSFORMS, MOARRR!!
    if (current != null) {
      tab().append("$.s(").append(eVar).append(",").append(getValueNow).append(");").newline();
    }
    // PULL from the current
    tab().append("_.").append(name).append(" = function(x) {").newline().tabUp();
    tab().append("$.s(").append(eVar).append(",").append(pullValue).append(");").newline().tabDown();
    // ALL SORTS OF FORMATTING FUN
    tab().append("};").newline();
    tab().append(parentVariable).append(".append(").append(eVar).append(");").newline();
    tabDown().tab().append("}").newline();
    pool.give(eVar);
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

  private void writeChildren(String current, Element element, boolean singleParent, String parentVariable) {
    NodeList list = element.getChildNodes();
    if (list != null && list.getLength() > 0) {
      for (int k = 0; k < list.getLength(); k++) {
        Node node = list.item(k);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element child = (Element) node;
          switch (child.getTagName()) {
            case "scope": {
              String into = child.getAttribute("into");
              String oldDelta = pool.ask();
              tab().append("{").tabUp().newline();
              tab().append("var ").append(oldDelta).append(" = _;").newline();
              tab().append("_ = {};").newline();
              writeChildren(current + "." + into, child, singleParent, parentVariable);
              tab().append(oldDelta).append(".").append(into).append(" = _;").newline();
              tab().append("_ = ").append(oldDelta).append(";").newline();
              tabDown().tab().append("}").newline();
              pool.give(oldDelta);
              break;
            }
            case "iterate": {
              if (!singleParent) {
                throw new UnsupportedOperationException("Yo");
              }
              writeIterate(null,  getSingleChild(child), child.getAttribute("name"), parentVariable);
              break;
            }
            case "lookup": {
              writeLookup(current, child.getAttribute("name"), child.getAttribute("transform"), parentVariable);
              break;
            }
            default: {
              writeElement("tree.tree", child, parentVariable, false);
              break;
            }
          }
        } else if (node.getNodeType() == Node.TEXT_NODE) {
          writeText((Text) node, parentVariable);
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
