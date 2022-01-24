/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.apikit;

import org.adamalang.apikit.codegen.*;
import org.adamalang.apikit.docgen.AssembleAPIDocs;
import org.adamalang.apikit.model.FieldDefinition;
import org.adamalang.apikit.model.Method;
import org.adamalang.apikit.model.ParameterDefinition;
import org.adamalang.apikit.model.Responder;
import org.adamalang.common.DefaultCopyright;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Tool {

  public static void build() throws Exception {
    // TODO: move into args
    FileInputStream input = new FileInputStream("saas/api.xml");
    Document doc = load(input);
    NodeList apiList = doc.getElementsByTagName("api");
    if (apiList == null || apiList.getLength() == 0) {
      throw new Exception("no root api node");
    }
    Element api = (Element) apiList.item(0);
    if (api == null) {
      throw new Exception("no root api node");
    }
    String outputPathStr = api.getAttribute("output-path");
    String packageName = api.getAttribute("package");
    if (outputPathStr == null || "".equals(outputPathStr)) {
      throw new Exception("no output path");
    }

    if (packageName == null || "".equals(packageName)) {
      throw new Exception("no package name");
    }
    Map<String, ParameterDefinition> parameters = ParameterDefinition.buildMap(doc);
    Map<String, FieldDefinition> fields = FieldDefinition.buildMap(doc);
    Map<String, Responder> responders = Responder.respondersOf(doc, fields);
    Method[] methods = Method.methodsOf(doc, parameters, responders);
    String nexus = AssembleNexus.make(packageName, parameters);
    Map<String, String> requestsFiles = AssembleRequestTypes.make(packageName, methods);
    Map<String, String> responderFiles = AssembleResponders.make(packageName, responders);
    Map<String, String> handlerFiles = AssembleHandlers.make(packageName, methods);
    String router = AssembleConnectionRouter.make(packageName, methods);
    String metrics = AssembleMetrics.make(packageName, methods);

    File outputPath = new File(outputPathStr);
    outputPath.mkdirs();
    if (!(outputPath.exists() && outputPath.isDirectory())) {
      throw new Exception("output path failed to be created");
    }
    HashMap<String, String> apiOutput = new HashMap<>();
    apiOutput.put("ConnectionNexus.java", nexus);
    apiOutput.put("ConnectionRouter.java", router);
    apiOutput.put("ApiMetrics.java", metrics);

    apiOutput.putAll(requestsFiles);
    apiOutput.putAll(responderFiles);
    apiOutput.putAll(handlerFiles);
    // write out the nexus

    for (Map.Entry<String, String> request : apiOutput.entrySet()) {
      Files.writeString(new File(outputPath, request.getKey()).toPath(), DefaultCopyright.COPYRIGHT_FILE_PREFIX + request.getValue());
    }
    // TODO: move to Schema
    Files.writeString(new File("apikit/docs/src/reference.md").toPath(), AssembleAPIDocs.docify(methods));
    Files.writeString(new File("client/src/api.ts").toPath(), AssembleClient.make(methods));
  }

  private static Document load(InputStream input) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(input);
  }

  public static void main(String[] args) throws Exception {
    build();
  }
}
