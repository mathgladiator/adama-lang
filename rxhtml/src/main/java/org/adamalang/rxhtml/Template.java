package org.adamalang.rxhtml;

import org.adamalang.rxhtml.atl.Parser;
import org.adamalang.rxhtml.atl.tree.Tree;
import org.adamalang.rxhtml.codegen.VariablePool;
import org.adamalang.rxhtml.codegen.Writer;
import org.w3c.dom.*;

import java.util.Set;

public class Template {
  private final Writer writer;
  private final VariablePool pool;

  private Template() {
    writer = new Writer();
    pool = new VariablePool();
  }


  private void writeRoot(Element element) {
    String rootVar = pool.ask();
    writer.append("_templates['").append(element.getAttribute("name")).append("'] = function(_tree, $) {").newline().tabUp();
    writer.tab().append("var _ = {};").newline();
    writer.tab().append("var ").append(rootVar).append(" = $.e('div');").newline();
    writeChildren("_tree.tree", element, true, rootVar);
    writer.tab().append("_tree.onTreeChange(_);").newline();
    writer.tab().append("return ").append(rootVar).append(";").newline();
    writer.append("}").newline();
  }

  private String writeElement(String current, Element element, String parentVariable, boolean returnVariable) {
    String eVar = pool.ask();
    writer.tab().append("var ").append(eVar).append(" = $.e('").append(element.getTagName()).append("');").newline();
    NamedNodeMap attrs = element.getAttributes();
    for (int k = 0; k < attrs.getLength(); k++) {
      Node attrNode = attrs.item(k);
      Attr attr = (Attr) attrNode;
      if (attr.getValue() != null) {
        Tree tree = Parser.parse(attr.getValue());
        Set<String> vars = tree.variables();
        if (vars.size() > 0) {
          var oVar = pool.ask();
          writer.tab().append("{").tabUp().newline();
          writer.tab().append("var ").append(oVar).append(" = {};").newline();
          // copy
          for (String var : vars) {
            writer.tab().append(oVar).append(".").append(var).append(" = ").append(current).append(".").append(var).append(";").newline();
          }
          writer.tab().append(oVar).append("._ = function() {").tabUp().newline();
          writer.tab().append(eVar).append(".").append(attr.getName()).append(" = '").append(tree.js(oVar)).append("';").newline();
          writer.tabDown().tab().append("}").newline();
          // TODO: for each variable, subscribe to an update, then fire _()
          // NOTE: handling multiple callbacks per field is a huge bug
          writer.tab().append(oVar).append("._();").newline();
          writer.tabDown().tab().append("}").newline();
        } else {
          // TODO: handle mapping of xml name to javascript name (or use function)
          writer.tab().append(eVar).append(".").append(attr.getName()).append(" = '").append(attr.getValue()).append("';").newline();
        }
      } else {
        // TODO: handle no-value
      }
    }
    writeChildren(current, element, element.getChildNodes().getLength() == 1, eVar);
    writer.tab().append(parentVariable).append(".append(").append(eVar).append(");").newline();
    if (returnVariable) {
      return eVar;
    } else {
      pool.give(eVar);
      return null;
    }
  }

  private void writeText(Text text, String parentVariable) {
    if (!text.getTextContent().trim().equalsIgnoreCase("")) {
      writer.tab().append(parentVariable).append(".append($.t('").append(text.getTextContent()).append("'));").newline();
    }
  }

  private void writeIterate(String current, Element template, String name, String parentVariable) {
    String setVar = pool.ask();
    String gidVar = pool.ask();
    writer.tab().append("var ").append(gidVar).append(" = $.g();").newline();
    writer.tab().append("_.").append(name).append(" = {").tabUp().newline();
    writer.tab().append("'+': function(").append(setVar).append(") {").tabUp().newline();
    writer.tab().append("var _ = {};").newline();
    String appendElement = writeElement(setVar + ".value", template, parentVariable, true);
    writer.tab().append(appendElement).append("._k = ").append(setVar).append(".key;").newline();
    writer.tab().append(setVar).append(".value['").append(setVar).append("_' + ").append(gidVar).append("] = ").append(appendElement).append(";").newline();
    pool.give(appendElement);
    writer.tab().append("return _;").newline();
    writer.tabDown().tab().append("},").newline();
    writer.tab().append("'-': function(").append(setVar).append(") {").tabUp().newline();
    writer.tab().append("$.n(").append(setVar).append(".before['").append(setVar).append("_' + ").append(gidVar).append("]);").newline();
    writer.tabDown().tab().append("},").newline();
    writer.tab().append("'^': function(").append(setVar).append(") {").tabUp().newline();
    writer.tab().append("$.r(").append(parentVariable).append(", ").append(setVar).append(");").newline();
    writer.tabDown().tab().append("}").newline();
    writer.tabDown().tab().append("}").newline();
    pool.give(setVar);
    pool.give(gidVar);
  };

  private void writeLookup(String current, String name, String transform, String parentVariable) {
    String eVar = pool.ask();
    writer.tab().append("{").tabUp().newline();
    writer.tab().append("var ").append(eVar).append(" = $.t(").append(current != null ? (current + "." + name) : "''").append(");").newline();
    String getValueNow = current + "." + name;
    String pullValue = "x.value";
    if ("ntclient.agent".equalsIgnoreCase(transform)) {
      getValueNow = getValueNow + ".agent";
      pullValue = pullValue + ".agent";
    }
    // MORE TRANSFORMS, MOARRR!!
    if (current != null) {
      writer.tab().append("$.s(").append(eVar).append(",").append(getValueNow).append(");").newline();
    }
    // PULL from the current
    writer.tab().append("_.").append(name).append(" = function(x) {").newline().tabUp();
    writer.tab().append("$.s(").append(eVar).append(",").append(pullValue).append(");").newline().tabDown();
    // ALL SORTS OF FORMATTING FUN
    writer.tab().append("};").newline();
    writer.tab().append(parentVariable).append(".append(").append(eVar).append(");").newline();
    writer.tabDown().tab().append("}").newline();
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
              writer.tab().append("{").tabUp().newline();
              writer.tab().append("var ").append(oldDelta).append(" = _;").newline();
              writer.tab().append("_ = {};").newline();
              writeChildren(current + "." + into, child, singleParent, parentVariable);
              writer.tab().append(oldDelta).append(".").append(into).append(" = _;").newline();
              writer.tab().append("_ = ").append(oldDelta).append(";").newline();
              writer.tabDown().tab().append("}").newline();
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
    return writer.toString();
  }

  public static String convertTemplateToJavaScript(Element element) {
    Template template = new Template();
    template.writeRoot(element);
    return template.toString();
  }
}
