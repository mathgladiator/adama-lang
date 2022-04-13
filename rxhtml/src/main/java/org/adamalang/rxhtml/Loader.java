package org.adamalang.rxhtml;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Loader {
  public static Document load(String xml) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), "input.xml");
  }
}
