package org.adamalang.clikit;

import org.adamalang.clikit.codegen.GroupClassGen;
import org.adamalang.clikit.codegen.MainGen;
import org.adamalang.clikit.exceptions.XMLFormatException;
import org.adamalang.clikit.model.ArgDefinition;
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
        String outputPath = cliElem.getAttribute("test-output-path");
        String packageName = cliElem.getAttribute("package");


        XMLFormatException exceptionTrack = new XMLFormatException();

        //Get all argument definitions
        Map<String, ArgDefinition> arguments = ArgDefinition.createMap(doc);

        NodeList groupNodes = doc.getElementsByTagName("group");

        // Create all the groups.
        Group[] groupList = Group.createGroupList(groupNodes, exceptionTrack, arguments);

        if (exceptionTrack.isActive) {
            throw exceptionTrack;
        }
        System.out.println("No XML errors...\nNow creating Files");

        for (Group group : groupList) {
            helpString.append(group.name + " ").append(group.documentation).append("\n");
        }

        String mainGen = MainGen.generate(groupList, packageName);
        Map<String, String> groupGens = GroupClassGen.generate(groupList, packageName);

        File mainJava = new File(outputPath, "Main.java");
        Map<File, String> fileStringMap = new TreeMap<>();

        fileStringMap.put(mainJava, mainGen);
        for (Map.Entry<String, String> entry: groupGens.entrySet()) {
            File file = new File(outputPath, Common.camelize(entry.getKey())+ ".java");
            fileStringMap.put(file, entry.getValue());
        }
        for (Map.Entry<File, String> entry: fileStringMap.entrySet()) {
            File file = entry.getKey();
            if (file.exists()) {
                System.out.println("File \"" + file.getName() + "\" exists, skipping...");
                continue;
            }
            Files.writeString(entry.getKey().toPath(), entry.getValue());
        }
        System.out.println("Done.");

        return mainGen;

    }

    public static void main(String[] args) throws Exception{
        buildFileSystem("./cli/example.xml");
    }






}