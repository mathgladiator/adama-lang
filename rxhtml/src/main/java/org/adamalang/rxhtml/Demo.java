package org.adamalang.rxhtml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class Demo {

  public static void main(String[] args) throws Exception {
    String xml = "<template name=\"demo\"><div><iterate name=\"chat\"><div><lookup name=\"who\" /> | <lookup name=\"what\" /></div></iterate></div><div class=\"x [b] y\" v=\"0\" id=\"attr\">name<scope into=\"z\">cake <lookup name=\"x\" /></scope><x><y>  </y></x><z></z></div></template>";
    Document document = Loader.load(xml);
    Element root = document.getDocumentElement();
    if (root.getTagName().equalsIgnoreCase("template")) {
      System.err.println("InputLength:" + xml.length());
      String output = Template.convertTemplateToJavaScript(root);
      System.err.println("OutputLength:" + output.length());
      ByteArrayOutputStream memory = new ByteArrayOutputStream();
      GZIPOutputStream gzip = new GZIPOutputStream(memory);
      gzip.write(output.getBytes(StandardCharsets.UTF_8));
      gzip.flush();
      gzip.close();
      System.err.println("GZipLength:" + memory.toByteArray().length);
      System.err.println(output);
    }
  }
}
