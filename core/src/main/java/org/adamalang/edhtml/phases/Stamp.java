package org.adamalang.edhtml.phases;

import org.adamalang.edhtml.EdHtmlState;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Stamp {
  public static void execute(EdHtmlState state) throws Exception {
    Elements stamps = state.document.getElementsByTag("stamp");
    for (Element stamp : stamps) {
      Elements bodyOfStamps = stamp.getElementsByTag("meat");
      if (bodyOfStamps.size() != 1) {
        throw new Exception("no meat within stamp");
      }
      Elements instances = stamp.getElementsByTag("instance");
      String name = stamp.attr("name");
      if (name == null) {
        throw new Exception("stamp has no name");
      }
      state.output_rx.append("<template name=\"").append(name).append("\">\n");
      for (Element instance : instances) {
        HashMap<String, String> defines = new HashMap<>();
        for (Attribute attr : instance.attributes()) {
          if (attr.getKey().startsWith("define:")) {
            defines.put(attr.getKey().substring(7), attr.getValue());
          }
        }
        String body = bodyOfStamps.html();
        for (Map.Entry<String, String> entry : defines.entrySet()) {
          body = body.replaceAll(Pattern.quote("%%" + entry.getKey()) + "%%", Matcher.quoteReplacement(entry.getValue()));
        }
        state.output_rx.append(body);
        state.output_rx.append("\n");
      }
      state.output_rx.append("</template>\n");
    }
  }
}
