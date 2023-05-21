package org.adamalang.cli.router;

import java.util.HashMap;
import java.util.Map;

public class CliElement {

    public String name;
    public Map<String, CliElement> Commands = new HashMap<>();
    public Map<String, ArgumentItem> Arguments = new HashMap<>();
    public static Map<String, ArgumentItem> fullArgumentList = populateArgs();
    public static Map<String, CliElement> Groups = populateGroup();
    public CliElement() {

    }
    public CliElement(HashMap<String, CliElement> commands, String name, String type) {
        this.name = name;
        if (type.equals("group")) {
            Commands = commands;
        }

    }

    public CliElement(HashMap<String, ArgumentItem> argument, String name) {
        this.name = name;
        Arguments = argument;
    }



    public static HashMap<String, CliElement> populateGroup() {
        HashMap<String, CliElement> returnMap = new HashMap<>();
        HashMap<String, CliElement> spaceCommands = new HashMap<>();
        HashMap<String, ArgumentItem> createSpaceArgs = new HashMap<>();
        //Copy the argument...
        createSpaceArgs.put("--space", fullArgumentList.get("--space"));
        spaceCommands.put("create", new CliElement(createSpaceArgs, "create"));
        returnMap.put("--space", new CliElement(spaceCommands, "--space", "group"));

        return returnMap;
    }

    public static HashMap<String, ArgumentItem> populateArgs() {
     // Create arguments and fill with documentation
    }

}
