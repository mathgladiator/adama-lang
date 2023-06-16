/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.clikit;

import org.adamalang.clikit.codegen.*;
import org.adamalang.clikit.exceptions.XMLFormatException;
import org.adamalang.clikit.model.ArgumentDefinition;
import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Common;
import org.adamalang.clikit.model.Group;
import org.adamalang.common.DefaultCopyright;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.TreeMap;

/** Tool to parse XML and create files according to the data **/
public class Tool {
    public static String buildFileSystem(String pathToXml) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        File xmlFile = new File(pathToXml);
        Document doc = PositionalXMLReader.readXML(xmlFile);
        NodeList cliList = doc.getElementsByTagName("cli");
        Node cliNode = Common.getFirstNode(cliList);
        Element cliElem = (Element) cliNode;
        String outputPath = cliElem.getAttribute("output-path");
        String testOutputPath = cliElem.getAttribute("test-output-path");
        String packageName = cliElem.getAttribute("package");
        XMLFormatException exceptionTrack = new XMLFormatException();
        //Get all argument definitions
        Map<String, ArgumentDefinition> arguments = ArgumentDefinition.createMap(doc, exceptionTrack);
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
        String handlerGen = RootHandlerGen.generate(groupList, mainCommandList ,packageName);
        String argumentTypeGen = ArgumentsGen.generate(groupList, mainCommandList ,packageName);
        String routerGen = MainRouterGen.generate(groupList, mainCommandList, packageName);
        String helpGen = HelpGen.generate(groupList, packageName);
        Map<String, String> routerGens = HandlerGen.generate(groupList, packageName);
        Map<String, String> stringMap = new TreeMap<>();
        Map<String, String> testGens = TestGen.generate(groupList, mainCommandList, packageName);
        stringMap.put("MainRouter.java", routerGen);
        stringMap.put("RootHandler.java", handlerGen);
        stringMap.put("Arguments.java", argumentTypeGen);
        stringMap.put("Help.java", helpGen);
        stringMap.putAll(routerGens);

        Map<File, String> fileStringMap = new TreeMap<>();
        for (Map.Entry<String, String> entry: stringMap.entrySet()) {
            String newValue = DefaultCopyright.COPYRIGHT_FILE_PREFIX + entry.getValue();
            File file = new File(outputPath, entry.getKey());
            fileStringMap.put(file, newValue);
        }
        for (Map.Entry<String, String> entry : testGens.entrySet()) {
            String newValue = DefaultCopyright.COPYRIGHT_FILE_PREFIX + entry.getValue();
            File file = new File(testOutputPath, entry.getKey());
            fileStringMap.put(file, newValue);
        }
        boolean mkdir = new File(outputPath).mkdir();
        boolean mkTestDir = new File(testOutputPath).mkdir();
        for (Map.Entry<File, String> entry: fileStringMap.entrySet()) {
            File file = entry.getKey();
            Files.writeString(file.toPath(), entry.getValue());
        }
        System.out.println("Done.");
        return "mainGen";

    }
    public static void main(String[] args) throws Exception{
        buildFileSystem("./cli/commands.xml");
    }
}