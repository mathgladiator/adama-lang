package org.adamalang.rxhtml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class Demo {

  public static void main(String[] args) throws Exception {
    // String xml = "<template name=\"demo\"><div><iterate name=\"chat\"><div><lookup name=\"who\" transform=\"ntclient.agent\" /> | <lookup name=\"what\" /></div></iterate></div></template>";
    // Document document = Loader.load(xml);
    Document document = Loader.load(new File("rxhtml/demo/templates.html"));
    Element root = document.getDocumentElement();
    if (root.getTagName().equalsIgnoreCase("template")) {
      String output = Template.convertTemplateToJavaScript(root);
      System.err.println(output);
    }
    if (root.getTagName().equalsIgnoreCase("forest")) {
      NodeList list = root.getChildNodes();
      for (int k = 0; k < list.getLength(); k++) {
        Node child = list.item(k);
        if (child.getNodeType() == Node.ELEMENT_NODE) {
          String output = Template.convertTemplateToJavaScript((Element) child);
          System.err.println(output);

        }
      }
    }
  }
}
