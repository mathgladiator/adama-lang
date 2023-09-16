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
package org.adamalang.cli.css;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CascadingStyleSheet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudyEngine {
  //String test = ".divide-opacity-0>:not([hidden])~:not([hidden])";
  private final static String WORD = "[a-z\\.\\-_\\\\/:%\\[\\]><~\\(\\)]*";

  // LIST not synced, pulled manually from study
  private final static String[] COLORS = new String[] {
      "pink", "cyan", "emerald", "black", "white", "zinc", "yellow", "violet", "fuchsia", "gray", "green", "transparent",
      "amber", "indigo", "lime", "teal", "stone", "sky", "rose", "neutral", "blue", "slate", "red", "orange", "purple"
  };
  private static final String[] COLOR_VARIANT = new String[] { "-100", "-200", "-300", "-400", "-500", "-600", "-700", "-800", "-900", "-950", "-50"};
  private static final String[] COLOR_ALPHA = new String[] {"/100", "/10", "/20", "/25", "/30", "/40", "/50", "/5", "/60", "/70", "/75", "/80", "/90", "/95"};

  public final static Pattern EXTRACT_SINGLE_NUMBER = Pattern.compile(WORD + "([0-9\\\\/]+(\\.[0-9]+)?)" + WORD);

  public static String extractColor(String rule) {
    for (String color : COLORS) {
      if (rule.contains("-" + color)) {
        for (String variant : COLOR_VARIANT) {
          if (rule.contains("-" + color + variant)) {
            for (String alpha : COLOR_ALPHA) {
              if (rule.contains("-" + color + variant + "\\\\" + alpha)) {
                return "-" + color + variant + "\\\\" + alpha;
              }
            }
            return "-" + color + variant;
          }
        }
        for (String alpha : COLOR_ALPHA) {
          if (rule.contains("-" + color + "\\\\" + alpha)) {
            return "-" + color + "\\\\" + alpha;
          }
        }
        return "-" + color;
      }
    }
    return null;
  }


  public static String study(CascadingStyleSheet css, StringBuilder constant_css, ObjectNode study) {
    StringBuilder result = new StringBuilder();
    for(CSSStyleRule rule : css.getAllStyleRules()) {
      if (rule.getSelectorCount() == 1) {
        String className = rule.getSelectorAtIndex(0).getAsCSSString();
        if (className.startsWith(".")) {
          String color = extractColor(className);
          if (color != null) {
            className = className.replaceAll(Pattern.quote(color), Matcher.quoteReplacement("$COLOR"));
          }
          Matcher matcher = EXTRACT_SINGLE_NUMBER.matcher(className);
          if (matcher.matches()) {
            className = className.replaceAll(Pattern.quote(matcher.group(1)), Matcher.quoteReplacement("$NUM"));
          }
          JsonNode child = study.get(className);
          if (child == null || child.isNull()) {
            child = study.putArray(className);
          }
          ((ArrayNode) child).add(rule.getAsCSSString());
        } else {
          constant_css.append(rule.getAsCSSString());
          constant_css.append("\n");
        }
      } else {
        constant_css.append(rule.getAsCSSString());
        constant_css.append("\n");
      }
    }

    return result.toString();
  }
}
