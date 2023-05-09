/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.edhtml.phases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.edhtml.EdHtmlState;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;
import java.util.function.Function;

/** the generator for taking a type and evaluating against a factory tree */
public class Generate {
  public static void execute(EdHtmlState state) throws Exception {
    HashMap<String, Element> factoryCache = new HashMap<>();
    HashMap<String, ObjectNode> reflectionCache = new HashMap<>();

    // get the factory (which may be cached between <generate> invocations) by name
    Function<String, Element> getFactory = (factoryName) -> {
      Element found = factoryCache.get(factoryName);
      if (found != null) {
        return found;
      }
      try {
        found = Jsoup.parse(new File(state.gen_path, factoryName + ".factory.html")).getElementsByTag("factory").get(0);
        factoryCache.put(factoryName, found);
        return found;
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    };

    // get the reflection (which may be cached between <generate> invocations) by filename
    Function<String, ObjectNode> getReflection = (filename) -> {
      ObjectNode found = reflectionCache.get(filename);
      if (found != null) {
        return found;
      }
      try {
        found = (ObjectNode) (new ObjectMapper().readTree(new File(state.base, filename)));
        reflectionCache.put(filename, found);
        return found;
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    };

    Elements generates = state.document.getElementsByTag("generate");
    for(Element generate : generates) {
      final ObjectNode from;
      if (generate.hasAttr("from")) {
        from = getReflection.apply(generate.attr("from"));
      } else if (state.document.hasAttr("default-from")) {
        from = getReflection.apply(state.document.attr("default-from"));
      } else {
        throw new RuntimeException("No 'from' field available for generate");
      }

      final Element factory;
      if (generate.hasAttr("factory")) {
        factory = getFactory.apply(generate.attr("factory"));
      } else {
        throw new RuntimeException("No 'factory' field available for generate");
      }

      // TODO: define a core "type manifest"

      if (generate.hasAttr("channel")) {
        String typeToUse = from.get("channels").get(generate.attr("channel")).textValue();
        // TODO: inject into "type manifest"
      }

      if (generate.hasAttr("type")) {
        String typeToUse = generate.attr("type");
        // TODO: if "type manifest" exists, then intersect
        // TODO: otherwise, inject into
      }

      // TODO: feed the type into the factory and get resulting RxHTML output
    }
  }
}
