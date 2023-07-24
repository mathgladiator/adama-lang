/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Tool {
  public static void build(String inputXmlFile, File root) throws Exception {
    HashMap<File, String> files = buildInMemoryFileSystem(inputXmlFile, root);
    for (Map.Entry<File, String> entry : files.entrySet()) {
      Files.writeString(entry.getKey().toPath(), entry.getValue());
    }
  }

  public static HashMap<File, String> buildInMemoryFileSystem(String inputXmlFile, File root) throws Exception {
    FileInputStream input = new FileInputStream(new File(root, inputXmlFile));
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(input);
    Element api = DocumentHelper.first(doc.getElementsByTagName("api"), "root api node");
    String outputPathStr = DocumentHelper.attribute(api, "output-path");
    String clientOutputPathStr = DocumentHelper.attribute(api, "client-output-path");
    String testOutputPathStr = DocumentHelper.attribute(api, "test-output-path");
    String packageName = DocumentHelper.attribute(api, "package");
    String docsFile = api.getAttribute("docs");
    String clientFileJs = api.getAttribute("clientjs");
    Map<String, ParameterDefinition> parameters = ParameterDefinition.buildMap(doc);
    Map<String, FieldDefinition> fields = FieldDefinition.buildMap(doc);
    Map<String, Responder> responders = Responder.respondersOf(doc, fields);
    Method[] methods = Method.methodsOf(doc, parameters, responders);
    String nexus = AssembleNexus.make(packageName, parameters);
    String devbox = AssembleDevBox.make(packageName, methods);
    Map<String, String> requestsFiles = AssembleRequestTypes.make(packageName, methods);
    Map<String, String> responderFiles = AssembleResponders.make(packageName, responders);
    Map<String, String> handlerFiles = AssembleHandlers.make(packageName, methods);
    Map<String, String> javaClientFiles = AssembleJavaClient.make(packageName, responders, methods);
    String router = AssembleConnectionRouter.make(packageName, methods);
    String metrics = AssembleMetrics.make(packageName, methods);
    File outputPath = new File(root, outputPathStr);
    File clientOutputPath = new File(clientOutputPathStr);
    outputPath.mkdirs();
    if (!(outputPath.exists() && outputPath.isDirectory())) {
      throw new Exception("output path failed to be created");
    }
    File testOutputPath = new File(root, testOutputPathStr);
    testOutputPath.mkdirs();
    if (!(testOutputPath.exists() && testOutputPath.isDirectory())) {
      throw new Exception("test output path failed to be created");
    }

    HashMap<String, String> apiOutput = new HashMap<>();
    apiOutput.put("DevBoxRouter.java", devbox);
    apiOutput.put("ConnectionNexus.java", nexus);
    apiOutput.put("ConnectionRouter.java", router);
    apiOutput.put("ApiMetrics.java", metrics);
    apiOutput.putAll(requestsFiles);
    apiOutput.putAll(responderFiles);
    apiOutput.putAll(handlerFiles);

    // write out the nexus
    HashMap<File, String> diskWrites = new HashMap<>();
    for (Map.Entry<String, String> request : apiOutput.entrySet()) {
      diskWrites.put(new File(outputPath, request.getKey()), DefaultCopyright.COPYRIGHT_FILE_PREFIX + request.getValue());
    }
    for (Map.Entry<String, String> request : AssembleTests.make(packageName, methods, responders).entrySet()) {
      diskWrites.put(new File(testOutputPath, request.getKey()), DefaultCopyright.COPYRIGHT_FILE_PREFIX + request.getValue());
    }
    if (docsFile != null && !docsFile.equals("")) {
      diskWrites.put(new File(root, docsFile), AssembleAPIDocs.docify(methods));
    }
    if (clientFileJs != null && !clientFileJs.equals("")) {
      String clientJs = Files.readString(new File(root, clientFileJs).toPath());
      clientJs = AssembleJavaScriptClient.injectInvokePlainJs(clientJs, methods);
      diskWrites.put(new File(root, clientFileJs), clientJs);
    }
    for (Map.Entry<String, String> javaClient : javaClientFiles.entrySet()) {
      diskWrites.put(new File(clientOutputPath, javaClient.getKey()), javaClient.getValue());
    }
    return diskWrites;
  }

  public static void main(String[] args) throws Exception {
    build("saas/api.xml", new File("."));
    build("saas/control.xml", new File("."));
  }

}
