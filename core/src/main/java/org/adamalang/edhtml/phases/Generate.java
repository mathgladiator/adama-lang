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
import org.adamalang.edhtml.phases.generate.AttributePair;
import org.adamalang.edhtml.phases.generate.Factory;
import org.adamalang.edhtml.phases.generate.FieldList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.function.Function;

/** the generator for taking a type and evaluating against a factory tree */
public class Generate {
  public static void execute(EdHtmlState state) throws Exception {
    HashMap<String, Factory> factoryCache = new HashMap<>();
    HashMap<String, ObjectNode> reflectionCache = new HashMap<>();

    // get the factory (which may be cached between <generate> invocations) by name
    Function<String, Factory> getFactory = (factoryName) -> {
      Factory found = factoryCache.get(factoryName);
      if (found != null) {
        return found;
      }
      try {
        String html = Files.readString(new File(state.gen_path, factoryName + ".factory.html").toPath());
        found = new Factory(Jsoup.parse(html, "", Parser.xmlParser()).getElementsByTag("factory").get(0));
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
    for (Element generate : generates) {
      final ObjectNode from;
      if (generate.hasAttr("from")) {
        from = getReflection.apply(generate.attr("from"));
      } else if (state.document.hasAttr("default-from")) {
        from = getReflection.apply(state.document.attr("default-from"));
      } else {
        throw new RuntimeException("No 'from' field available for generate");
      }

      final Factory factory;
      if (generate.hasAttr("factory")) {
        factory = getFactory.apply(generate.attr("factory"));
      } else {
        throw new RuntimeException("No 'factory' field available for generate");
      }

      HashMap<String, String> defines = new HashMap<>();

      FieldList manifest = null;
      if (generate.hasAttr("channel")) {
        String channelToUse = generate.attr("channel");
        defines.put("channel", channelToUse);
        String typeToUse = from.get("channels").get(channelToUse).textValue(); // TODO: validate
        manifest = FieldList.of((ObjectNode) from.get("types").get(typeToUse));
      }

      if (generate.hasAttr("type")) {
        String typeToUse = generate.attr("type");
        if (manifest == null) {
          manifest = FieldList.of((ObjectNode) from.get("types").get(typeToUse)); // TODO: validate
        } else {
          manifest = FieldList.intersect(manifest, FieldList.of((ObjectNode) from.get("types").get(typeToUse))); // TODO: validate
        }
      }

      if (manifest == null) {
        throw new RuntimeException("no manifest; need either a channel or type (or both)");
      }

      for (Attribute attribute : generate.attributes()) {
        AttributePair ap = new AttributePair(attribute.getKey());
        if ("define".equals(ap.key)) {
          defines.put(ap.value, attribute.getValue());
        }
      }

      state.output_rx.append(factory.produce(generate.attr("name"), manifest, defines));
    }
  }
}
