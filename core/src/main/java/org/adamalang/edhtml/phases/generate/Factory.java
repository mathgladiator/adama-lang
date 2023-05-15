/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.edhtml.phases.generate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** a factory for turning a field list into an RxHTML snippet */
public class Factory {
  private final String root;
  private final ArrayList<Rule> rules;

  public Factory(Element element) {
    root = element.getElementsByTag("root").get(0).html();
    rules = new ArrayList<>();
    for (Element ruleElement : element.getElementsByTag("rule")) {
      rules.add(new Rule(ruleElement));
    }
  }

  public String produce(String name, FieldList fields, HashMap<String, String> defines) {
    for (Field field : fields) {
      for (Rule rule : rules) {
        if (rule.test(field)) {
          rule.manifest(field, defines);
          break;
        }
      }
    }
    String next = root;
    for (Map.Entry<String, String> entry : defines.entrySet()) {
      next = next.replaceAll(Pattern.quote("%%" + entry.getKey()) + "%%", Matcher.quoteReplacement(entry.getValue()));
    }
    return Jsoup.parse("<template name=\"" + name + "\">" + next + "</template>").getElementsByTag("template").outerHtml();
  }
}
