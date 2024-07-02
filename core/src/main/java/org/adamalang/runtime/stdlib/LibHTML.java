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
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.PrimitiveIterator;

/** Add HTML <- / -> JSON capability */
public class LibHTML {

  // support: which HTML5 tags self close
  private static boolean __selfClosing(String tag) {
    switch (tag.toLowerCase(Locale.ENGLISH).trim()) {
      case "area":
      case "base":
      case "br":
      case "col":
      case "embed":
      case "hr":
      case "img":
      case "input":
      case "link":
      case "meta":
      case "param":
      case "source":
      case "track":
      case "wbr":
        return true;
      default:
        return false;
    }
  }

  // the recursive conversion function (HTML -> JSON)
  private static void __convertElement(Element element, JsonStreamWriter writer) {
    // <element>
    writer.beginObject();
    // [tag name]
    writer.writeObjectFieldIntro("t");
    writer.writeString(element.tagName());

    // [attributes]
    if (element.attributes().size() > 0) {
      writer.writeObjectFieldIntro("a");
      writer.beginObject();
      for (Attribute attribute : element.attributes()) {
        writer.writeObjectFieldIntro(attribute.getKey());
        if (attribute.hasDeclaredValue()) {
          writer.writeString(attribute.getValue());
        } else {
          writer.writeNull();
        }
      }
      writer.endObject();
    }

    if (!__selfClosing(element.tagName()) || element.childNodes().size() > 0) {
      // [children]
      writer.writeObjectFieldIntro("c");
      writer.beginArray();
      for (Node child : element.childNodes()) {
        if (child instanceof Element) {
          __convertElement((Element) child, writer);
        } else if (child instanceof TextNode) {
          writer.writeString(((TextNode) child).text());
        } // don't care about other things at this time
      }
      writer.endArray();
    }
    // </element>
    writer.endObject();
  }

  @Extension
  public static @HiddenType(clazz = NtDynamic.class) NtMaybe<NtDynamic> convertHTMLtoJSON(String html, String rootTag) {
    Document doc = Jsoup.parse(html);
    Elements elements = doc.getElementsByTag(rootTag);
    if (elements.size() == 1) {
      JsonStreamWriter writer = new JsonStreamWriter();
      __convertElement(elements.get(0), writer);
      return new NtMaybe<>(new NtDynamic(writer.toString()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static String escapeHTMLAttributeValue(String value) {
    StringBuilder result = new StringBuilder();
    PrimitiveIterator.OfInt it = value.codePoints().iterator();
    while (it.hasNext()) {
      int cp = it.nextInt();
      switch (cp) {
        case '"':
          result.append("&quot;");
          break;
        case '\'':
          result.append("&apos;");
          break;
        case '&':
          result.append("&amp;");
          break;
        case '<':
          result.append("&lt;");
          break;
        case '>':
          result.append("&gt;");
          break;
        default:
          result.append(Character.toString(cp));
      }
    }
    return result.toString();
  }

  // the recursive conversion function (JSON -> HTML)
  private static void __convertTree(Object tree, StringBuilder html) {
    if (tree == null) {
      return;
    }
    if (tree instanceof Map) {
      Map<String, Object> element = (Map<String, Object>) tree;
      Object tagName = element.get("t");
      if (tagName != null && tagName instanceof String) {
        html.append("<").append(tagName);
        Object attrRaw = element.get("a");
        if (attrRaw != null && attrRaw instanceof Map) {
          Map<String, Object> attributes = (Map<String, Object>) attrRaw;
          for(Map.Entry<String, Object> attr : attributes.entrySet()) {
            html.append(" ").append(attr.getKey());
            Object valRaw = attr.getValue();
            if (valRaw != null && valRaw instanceof String) {
              String value = (String) valRaw;
              html.append("=\"").append(escapeHTMLAttributeValue(value)).append("\"");
            }
          }
        }
        Object children = element.get("c");
        if (children == null) {
          html.append(" />");
        } else {
          html.append(">");
          __convertTree(element.get("c"), html);
          html.append("</").append(tagName).append(">");
        }
      }
    } else if (tree instanceof ArrayList) {
      ArrayList<Object> children = (ArrayList<Object>) tree;
      for (Object child : children) {
        __convertTree(child, html);
      }
    } else {
      html.append(tree);
    }
  }

  @Extension
  public static String convertJSONtoHTML(NtDynamic json) {
    StringBuilder html = new StringBuilder();
    __convertTree(json.cached(), html);
    return html.toString();
  }
}
