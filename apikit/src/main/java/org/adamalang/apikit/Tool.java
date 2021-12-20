package org.adamalang.apikit;

import org.adamalang.apikit.codegen.*;
import org.adamalang.apikit.docgen.AssembleAPIDocs;
import org.adamalang.apikit.model.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Tool {

    private static Document load(InputStream input) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(input);
    }

    public static void main(String[] args) throws Exception {
        FileInputStream input = new FileInputStream("apikit/api.xml");
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

        File outputPath = new File(outputPathStr);
        outputPath.mkdirs();
        if (!(outputPath.exists() && outputPath.isDirectory())) {
            throw new Exception("output path failed to be created");
        }
        HashMap<String, String> apiOutput = new HashMap<>();
        apiOutput.put("ConnectionNexus.java", nexus);
        apiOutput.put("ConnectionRouter.java", router);
        apiOutput.putAll(requestsFiles);
        apiOutput.putAll(responderFiles);
        apiOutput.putAll(handlerFiles);
        // write out the nexus

        for (Map.Entry<String, String> request : apiOutput.entrySet()) {
            Files.writeString(new File(outputPath, request.getKey()).toPath(), request.getValue());
        }


        Files.writeString(new File("apikit/docs/src/reference.md").toPath(), AssembleAPIDocs.docify(methods));
    }
}
