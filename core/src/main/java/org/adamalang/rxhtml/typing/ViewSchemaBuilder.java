package org.adamalang.rxhtml.typing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.rxhtml.acl.Parser;
import org.adamalang.rxhtml.acl.commands.Command;
import org.adamalang.rxhtml.atl.ParseException;
import org.adamalang.rxhtml.template.Base;
import org.adamalang.rxhtml.template.Root;
import org.adamalang.rxhtml.template.config.Feedback;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Stack;
import java.util.TreeMap;

/** construct a view schema */
public class ViewSchemaBuilder {
  private final Document document;
  private final Feedback feedback;
  private final TreeMap<String, Elements> templates;
  private Stack<Elements> fragments;

  public final ObjectNode results;

  public ViewSchemaBuilder(Document document, Feedback feedback) {
    this.document = document;
    this.feedback = feedback;
    this.templates = new TreeMap<>();
    for (Element element : document.getElementsByTag("template")) {
      templates.put(element.attr("name"), element.children());
    }
    this.fragments = new Stack<>();
    this.results = Json.newJsonObject();
    for (Element element : document.getElementsByTag("page")) {
      Root.Instructions uriInstructions = Root.uri_to_instructions(element.attr("uri"));
      String normalizedUri = uriInstructions.normalized;
      ViewScope vs = ViewScope.makeRoot();
      vs.types.putAll(uriInstructions.types);
      buildSchema(element.children(), vs);
      vs.fill(results.putObject(normalizedUri));
    }
  }

  public void buildSchema(Elements children, ViewScope schema) {
    for (Element child : children) {
      if ("fragment".equals(child.tagName())) {
        buildSchema(fragments.peek(), schema);
        return;
      }
      boolean expand = child.hasAttr("rx:expand-view-state");
      ViewScope current = schema;
      if (expand) {
        if (child.hasAttr("rx:scope")) {
          current = current.eval(child.attr("rx:scope"));
        }
        if (child.hasAttr("rx:iterate")) {
          current = current.eval(child.attr("rx:iterate"));
        }
      }
      for (String event : Base.EVENTS) {
        if (child.hasAttr("rx:" + event)) {
          try {
            for (Command command : Parser.parse(child.attr("rx:" + event))) {
              command.writeType(current);
            }
          } catch (ParseException pe) {
          }
        }
      }
      // IF/IFNOT (kind of tricky)

      if (child.hasAttr("rx:template")) {
        fragments.push(child.children());
        buildSchema(templates.get(child.attr("rx:template")), current);
        fragments.pop();
      } else {
        buildSchema(child.children(), current);
      }
    }
  }
}
