package org.adamalang.apikit;

import org.adamalang.apikit.codegen.*;
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
        FileInputStream input = new FileInputStream("web/api.xml");
        Document doc = load(input);
        Map<String, ParameterDefinition> parameters = ParameterDefinition.buildMap(doc);
        Method[] methods = Method.methodsOf(doc, parameters);

        String packageName = "org.adamalang.web.api";
        String nexus = AssembleNexus.make(packageName, parameters);
        Map<String, String> requests = AssembleRequestTypes.make(packageName, methods);

        String outputPathStr = "web/src/main/java/org/adamalang/web/api";
        File outputPath = new File(outputPathStr);
        if (!(outputPath.exists() && outputPath.isDirectory())) {
            throw new Exception("path does not exist");
        }

        // write out the nexus
        Files.writeString(new File(outputPath, "Nexus.java").toPath(), nexus);
        for (Map.Entry<String, String> request : requests.entrySet()) {
            Files.writeString(new File(outputPath, request.getKey()).toPath(), request.getValue());
        }
    }
}
