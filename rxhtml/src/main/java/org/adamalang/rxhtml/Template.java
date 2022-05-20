package org.adamalang.rxhtml;

import org.adamalang.rxhtml.atl.Parser;
import org.adamalang.rxhtml.atl.tree.Tree;
import org.adamalang.rxhtml.codegen.VariablePool;
import org.adamalang.rxhtml.codegen.Writer;
import org.jsoup.nodes.*;

import java.util.HashMap;
import java.util.Set;

public class Template {
  private final Writer writer;
  private final VariablePool pool;

  public Template() {
    writer = new Writer();
    pool = new VariablePool();
    writer.append(" function install($) {").tabUp().newline();
  }

  public String finish() {
    writer.tabDown().tab().append("}").newline();
    return writer.toString();
  }

  public void writeRoot(Element element) {
    String rootVar = pool.ask();
    writer.tab().append("$.register('").append(element.attr("name")).append("', function(_tree) {").newline().tabUp();
    writer.tab().append("var _ = {};").newline();
    writer.tab().append("var ").append(rootVar).append(" = $.e('div');").newline();
    writeChildren("_tree.tree", element, true, rootVar, new HashMap<>());
    writer.tab().append("_tree.onTreeChange(_);").newline();
    writer.tab().append("return ").append(rootVar).append(";").newline();
    writer.tabDown().tab().append("});").newline();
  }

  private String convertXmlAttributeToJavaScriptField(String realKey) {
    switch (realKey) {
      case "class":
        return "className";
      case "aria-hidden":
        return "ariaHidden";
      case "fill-rule":
        return "fillRule";
      case "clip-rule":
        return "clipRule";
      case "for":
        return "htmlFor";
    }
  }

  private String writeElement(String current, Element element, String parentVariable, boolean returnVariable, HashMap<String, Integer> subscriptionCounts) {
    String eVar = pool.ask();
    writer.tab().append("var ").append(eVar).append(" = $.e('").append(element.tagName()).append("');").newline();
    for (Attribute attr : element.attributes().asList()) {
      if (attr.getValue() != null) {
        Tree tree = Parser.parse(attr.getValue());
        Set<String> vars = tree.variables();
        String realKey = attr.getKey();
        // TODO: expand mapping of attribute names

        if (vars.size() > 0) {
          var oVar = pool.ask();
          writer.tab().append("{").tabUp().newline();
          writer.tab().append("var ").append(oVar).append(" = {};").newline();
          // copy
          for (String var : vars) {
            writer.tab().append(oVar).append(".").append(var).append(" = ").append(current).append(".").append(var).append(";").newline();
          }
          writer.tab().append(oVar).append("._ = function() {").tabUp().newline();
          writer.tab().append(eVar).append(".").append(realKey).append(" = '").append(tree.js(oVar)).append("';").newline();
          writer.tabDown().tab().append("}").newline();
          // TODO: for each variable, subscribe to an update, then fire _()
          // NOTE: handling multiple callbacks per field is a huge bug
          writer.tab().append(oVar).append("._();").newline();
          writer.tabDown().tab().append("}").newline();
        } else {
          writer.tab().append(eVar).append(".").append(realKey).append(" = '").append(attr.getValue()).append("';").newline();
        }
      } else {
        // TODO: handle no-value
      }
    }
    writeChildren(current, element, element.children().size() == 1, eVar, subscriptionCounts);
    writer.tab().append(parentVariable).append(".append(").append(eVar).append(");").newline();
    if (returnVariable) {
      return eVar;
    } else {
      pool.give(eVar);
      return null;
    }
  }

  private void writeText(TextNode text, String parentVariable) {
    if (!text.text().trim().equalsIgnoreCase("")) {
      writer.tab().append(parentVariable).append(".append($.t('").append(text.text()).append("'));").newline();
    }
  }

  private void writeIterate(String current, Element template, String name, String parentVariable, HashMap<String, Integer> subscriptionCounts) {
    String setVar = pool.ask();
    String gidVar = pool.ask();
    writer.tab().append("var ").append(gidVar).append(" = $.g();").newline();
    writer.tab().append("_.").append(name).append(" = {").tabUp().newline();
    writer.tab().append("'+': function(").append(setVar).append(") {").tabUp().newline();
    writer.tab().append("var _ = {};").newline();
    String appendElement = writeElement(setVar + ".value", template, parentVariable, true, subscriptionCounts);
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

  private String wrapTransform(String expression, String transform) {
    if (transform == null || "".equals(transform)) {
      return expression;
    }
    if ("ntclient.agent".equalsIgnoreCase(transform)) {
      return expression + ".agent";
    }
    return expression;
  }

  private void writeLookup(String current, String name, String transform, String parentVariable, HashMap<String, Integer> subscriptionCounts) {
    String eVar = pool.ask();
    writer.tab().append("{").tabUp().newline();
    writer.tab().append("var ").append(eVar).append(" = $.t(").append(wrapTransform(current != null ? (current + "." + name) : "''", transform)).append(");").newline();
    SubscribeMethod method = subscribeTest(name, subscriptionCounts);
    switch (method) {
      case Set:
        writer.tab().append("_.").append(name).append(" = ");
        break;
      case TurnIntoArray:
        writer.tab().append("_.").append(name).append(" = [_.").append(name).append(",");
        break;
      case Push:
        writer.tab().append("_.").append(name).append(".push(");
        break;
    }
    writer.append("function(x) {").newline().tabUp();
    writer.tab().append("$.s(").append(eVar).append(",").append(wrapTransform("x.value", transform)).append(");").newline().tabDown();
    writer.tab().append("}");
    switch (method) {
      case Set:
        writer.append(";");
        break;
      case TurnIntoArray:
        writer.append("];");
        break;
      case Push:
        writer.append(");");
        break;
    }
    writer.newline();
    writer.tab().append(parentVariable).append(".append(").append(eVar).append(");").newline();
    writer.tabDown().tab().append("}").newline();
    pool.give(eVar);
  }

  private enum SubscribeMethod {
    Set,
    TurnIntoArray,
    Push
  }

  private SubscribeMethod subscribeTest(String name, HashMap<String, Integer> subscriptionCounts) {
    Integer prior = subscriptionCounts.get(name);
    if (prior == null) {
      subscriptionCounts.put(name, 1);
      return SubscribeMethod.Set;
    } else if (prior == 1) {
      subscriptionCounts.put(name, 2);
      return SubscribeMethod.TurnIntoArray;
    } else {
      subscriptionCounts.put(name, prior + 1);
      return SubscribeMethod.Push;
    }
  }

  private Element getSingleChild(Element element) {

    return element;
  }

  private void writeChildren(String current, Element element, boolean singleParent, String parentVariable, HashMap<String, Integer> subscriptionCounts) {
    for (int k = 0; k < element.childNodeSize(); k++) {
      Node node = element.childNode(k);
      if (node instanceof TextNode) {
        writeText((TextNode) node, parentVariable);
      } else if (node instanceof Comment) {
        // ignore comments
      } else if (node instanceof Element) {
        Element child = (Element) node;
        switch (child.tagName()) {
          case "scope": {
            String into = child.attr("into");
            String oldDelta = pool.ask();
            writer.tab().append("{").tabUp().newline();
            writer.tab().append("var ").append(oldDelta).append(" = _;").newline();
            writer.tab().append("_ = {};").newline();
            writeChildren(current + "." + into, child, singleParent, parentVariable, new HashMap<>());
            writer.tab().append(oldDelta).append(".").append(into).append(" = _;").newline();
            writer.tab().append("_ = ").append(oldDelta).append(";").newline();
            writer.tabDown().tab().append("}").newline();
            pool.give(oldDelta);
            break;
          }
          case "iterate": {
            if (!singleParent) {
              throw new UnsupportedOperationException("<iterate> must have a single parent as the parent will be the container");
            }
            if (element.childNodeSize() != 1 || !(element.childNode(0) instanceof Element)) {
              throw new UnsupportedOperationException("<iterate> must have a single children which will be repeated");
            }
            writeIterate(null,  child.child(0), child.attr("name"), parentVariable, new HashMap<>());
            break;
          }
          case "lookup": {
            writeLookup(current, child.attr("name"), child.attr("transform"), parentVariable, subscriptionCounts);
            break;
          }
          default: {
            writeElement(current, child, parentVariable, false, subscriptionCounts);
            break;
          }
        }
      } else {
        throw new UnsupportedOperationException("not sure how to handle node type:" + node.getClass());
      }
    }
  }

  @Override
  public String toString() {
    return writer.toString();
  }
}
