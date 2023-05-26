package org.adamalang.clikit;

import org.adamalang.clikit.codegen.*;
import org.adamalang.clikit.exceptions.XMLFormatException;
import org.adamalang.clikit.model.ArgDefinition;
import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Common;
import org.adamalang.clikit.model.Group;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.TreeMap;

public class Tool {
    /*
    * This is for parsing XML and getting usable data (to start)
    * For this example, we want to dissect the example xml and get the dogs,
    * and their treat count, they should be put inside a dog model. Treats can
    * be just a simple field for now.
    *
    * End goal is for this to turn to a construct that can take an xml and turn it into
    * a class. Essentially, the given xml will just be info of classes to be made.
    */

    

    public static String buildFileSystem(String pathToXml) throws Exception{
        StringBuilder helpString = new StringBuilder();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File xmlFile = new File(pathToXml);
        Document doc = builder.parse(xmlFile);
        NodeList cliList = doc.getElementsByTagName("cli");
        Node cliNode = Common.getFirstNode(cliList);
        Element cliElem = (Element) cliNode;
        String outputPath = cliElem.getAttribute("output-path");
        String testOutputPath = cliElem.getAttribute("test-output-path");
        String packageName = cliElem.getAttribute("package");

        XMLFormatException exceptionTrack = new XMLFormatException();

        //Get all argument definitions
        Map<String, ArgDefinition> arguments = ArgDefinition.createMap(doc);

        NodeList groupNodes = doc.getElementsByTagName("group");
        NodeList commandNodes = doc.getElementsByTagName("command");
        // Create all the groups.
        Group[] groupList = Group.createGroupList(groupNodes, exceptionTrack, arguments);

        // Main commands in main group

        Command[] mainCommandList = Command.createCommandList(commandNodes, exceptionTrack, arguments, "cli");




        if (exceptionTrack.isActive) {
            throw exceptionTrack;
        }
        System.out.println("No XML errors...\nNow creating Files");

        for (Group group : groupList) {
            helpString.append(group.name + " ").append(group.documentation).append("\n");
        }
        String mainGen = MainRouterGen.generate(groupList, mainCommandList ,packageName);
        String cliGen = CliElementGen.generate(arguments, groupList, mainCommandList, packageName);
        String argumentTypeGen = ArgumentTypeGen.generate(groupList, mainCommandList ,packageName);
        Map<String, String> routerGens = HandlerGen.generate(groupList, packageName);
        Map<String, String> stringMap = new TreeMap<>();
        Map<String, String> testGens = TestGen.generate(groupList, mainCommandList, packageName);
        stringMap.put("RootHandler.java", mainGen);
        stringMap.put("CliElement.java", cliGen);
        stringMap.put("ArgumentType.java", argumentTypeGen);
        stringMap.putAll(routerGens);

        Map<File, String> fileStringMap = new TreeMap<>();
        for (Map.Entry<String, String> entry: stringMap.entrySet()) {
            File file = new File(outputPath, entry.getKey());
            fileStringMap.put(file, entry.getValue());
        }
        for (Map.Entry<String, String> entry : testGens.entrySet()) {
            File file = new File(testOutputPath, entry.getKey());
            fileStringMap.put(file, entry.getValue());
        }


        boolean mkdir = new File(outputPath).mkdir();
        boolean mkTestDir = new File(testOutputPath).mkdir();
        for (Map.Entry<File, String> entry: fileStringMap.entrySet()) {
            File file = entry.getKey();
            Files.writeString(file.toPath(), entry.getValue());
        }
        System.out.println("Done.");

        return mainGen;

    }

    public static void main(String[] args) throws Exception{
        buildFileSystem("./cli/example.xml");
    }






}