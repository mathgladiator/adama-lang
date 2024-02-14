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
package org.adamalang.apikit;

import org.adamalang.apikit.codegen.*;
import org.adamalang.apikit.codegen.AssembleAPIDocs;
import org.adamalang.apikit.model.*;
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
import java.util.TreeSet;

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
    TreeSet<String> scopes = Isolate.scopesOf(doc);
    String outputPathStr = DocumentHelper.attribute(api, "output-path");
    String clientOutputPathStr = DocumentHelper.attribute(api, "client-output-path");
    String testOutputPathStr = DocumentHelper.attribute(api, "test-output-path");
    String packageName = DocumentHelper.attribute(api, "package");
    String sessionImport = DocumentHelper.attribute(api, "session-import");
    String outputAdamaService = DocumentHelper.attribute(api, "output-service");
    String docsFile = api.getAttribute("docs");
    String clientFileJs = api.getAttribute("clientjs");
    HashMap<String, String> apiOutput = new HashMap<>();
    Map<String, FieldDefinition> fields = FieldDefinition.buildMap(doc);
    Map<String, ParameterDefinition> parameters = ParameterDefinition.buildMap(doc);
    Map<String, Responder> responders = Responder.respondersOf(doc, fields);
    Method[] methods = Method.methodsOf(doc, parameters, responders);
    Map<String, String> handlerFiles = AssembleHandlers.make(packageName, sessionImport, methods);
    Map<String, String> javaClientFiles = AssembleJavaClient.make(packageName, responders, methods);
    String devbox = AssembleDevBox.make(packageName, methods);
    String defaultPolicy = AssembleDefaultPolicy.make_default_policy_as_code(packageName, methods);
    for (String scope : scopes) {
      String scopePrefix = Common.camelize(scope);
      Map<String, ParameterDefinition> isolatedParameters = Isolate.scopeParameters(doc, parameters, scope);
      String nexus = AssembleNexus.make(packageName, scopePrefix, isolatedParameters);
      Method[] scopedMethods = Isolate.scopeMethods(methods, scope);
      String router = AssembleConnectionRouter.make(packageName, sessionImport, scopePrefix, scopedMethods);
      String metrics = AssembleMetrics.make(packageName, scopePrefix, scopedMethods);

      apiOutput.put(scopePrefix + "ConnectionNexus.java", nexus);
      apiOutput.put(scopePrefix + "ConnectionRouter.java", router);
      apiOutput.put(scopePrefix + "ApiMetrics.java", metrics);
    }
    Map<String, String> requestsFiles = AssembleRequestTypes.make(packageName, sessionImport, methods);
    Map<String, String> responderFiles = AssembleResponders.make(packageName, responders);
    String adamaService = AssembleServiceDefn.assembleAdamaService(methods);


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

    apiOutput.put("DevBoxRouter.java", devbox);
    apiOutput.put("DefaultPolicy.java", defaultPolicy);
    apiOutput.putAll(requestsFiles);
    apiOutput.putAll(responderFiles);
    apiOutput.putAll(handlerFiles);

    // write out the nexus
    HashMap<File, String> diskWrites = new HashMap<>();
    diskWrites.put(new File(outputAdamaService), adamaService);
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
      diskWrites.put(new File(clientOutputPath, javaClient.getKey()), DefaultCopyright.COPYRIGHT_FILE_PREFIX + javaClient.getValue());
    }
    return diskWrites;
  }

  public static void main(String[] args) throws Exception {
    build("saas/api.xml", new File("."));
  }

}
