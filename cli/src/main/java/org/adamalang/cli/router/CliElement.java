package org.adamalang.cli.router;

import java.util.HashMap;
import java.util.Map;

public class CliElement {
  public String name;
  public Map<String, CliElement> Commands = new HashMap<>();
  public Map<String, ArgumentItem> Arguments = new HashMap<>();
  public static Map<String, ArgumentItem> fullArgumentList = populateArgs();
  public static Map<String, CliElement> Groups = populateGroups();

  public CliElement(HashMap<String, CliElement> commands, String name, String type) {
    this.name = name;
    if (type.equals("group")) {
      Commands = commands;
    }
  }

  public CliElement(HashMap<String, ArgumentItem> arguments, String name) {
    this.name = name;
    Arguments = arguments;
  }
  public static HashMap<String, CliElement> populateGroups() {
    HashMap<String, CliElement> returnMap = new HashMap<>();

    HashMap<String, CliElement> spaceCommands = new HashMap<>();
    HashMap<String, ArgumentItem> createSpaceArgs = new HashMap<>();
    createSpaceArgs.put("--space", fullArgumentList.get("--space"));
    spaceCommands.put("create", new CliElement(createSpaceArgs, "create"));
    returnMap.put("space", new CliElement(spaceCommands, "space", "group"));

    return returnMap;
  }

  public static HashMap<String, ArgumentItem> populateArgs() {
    HashMap<String, ArgumentItem> argList = new HashMap<>();
    argList.put("--space",new ArgumentItem("--space", "-s", "A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to denote the name of that collection."));
    return argList;
  }
}