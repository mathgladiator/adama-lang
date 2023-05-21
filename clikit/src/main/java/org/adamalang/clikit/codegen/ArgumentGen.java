package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.ArgDefinition;
import org.adamalang.clikit.model.Argument;
import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Group;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentGen {
    public static String escape(String s){
        Pattern pattern = Pattern.compile("\\s{2,}");

        String returnString = s.replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                .replace("\n", " ");

        Matcher matcher = pattern.matcher(returnString);
        returnString = matcher.replaceAll(" ");
        return returnString;
    }
    public static String generate(Map<String, ArgDefinition> argDefinitions, Group[] groups, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import java.util.Map;\n");
        sb.append("import java.util.HashMap;\n\n");

        sb.append("public class ArgumentObj {\n");
        sb.append("  public static Map<String, Argument> fullArgumentMap = argumentMap();\n");
        sb.append("  public Map<String, Argument> currentMap = new HashMap<String, Argument>();\n");


        sb.append("  public String group;\n");
        sb.append("  public String command;\n");





        /* This will require 3 hashmaps to work correctly, is it required?
        * TODO: If another interface/class requires hashmaps, then we will have to change ideas...
        */


        sb.append("  public ArgumentObj(String[] args) {\n");
        sb.append("    if (args.length >= 1) {\n");
        sb.append("      group = args[0];\n");
        sb.append("    } else {\n");
        sb.append("      Help.displayHelp();\n");
        sb.append("    }\n");
        sb.append("    if (args.length >= 2) {\n");
        sb.append("      command = args[1];\n");
        sb.append("    } else {\n");
        sb.append("      Help.displayHelp(group);\n");
        sb.append("    }\n");

        //Make sure both the group and command are valid.

        // Second element should be the command, if it is empty, show the help for that group.

        // If the second element is there, and there is help then display help for THAT COMMAND.
        sb.append("    for (int i = 2; i < args.length ; i+=2 ) {\n");
        // Make changes so it also allows the shortened version
        // Obvious way is to make another map to the real strings.
        // TODO: Could have a flag instead of variable, may need to adjust for that.
        sb.append("      Argument givenArg = fullArgumentMap.getOrDefault(args[i], null);\n");
        sb.append("      if (givenArg != null) {\n");
        sb.append("        Argument copyArg = new Argument(givenArg);\n");
        // Could not exist, so adjust for that...
        sb.append("        copyArg.value = args[i+1];\n");
        sb.append("        currentMap.put(copyArg.name, copyArg);\n");
        sb.append("      } else {\n");
        sb.append("        //addException;\n");
        sb.append("      }\n");
        sb.append("      \n");
        sb.append("    }\n");
        sb.append("  }\n");
        // After it is done, check if it is asking for help, and activate help.

        sb.append("  private static Map<String, Argument> argumentMap() {\n");
        sb.append("    Map<String, Argument> returnMap = new HashMap<>();\n");
        for (Map.Entry<String, ArgDefinition> entry: argDefinitions.entrySet()) {
            ArgDefinition argDef = entry.getValue();
            sb.append("    Argument ").append(entry.getKey()).append(" = new Argument(\"").append(entry.getKey()).append("\" , \"").append(escape(argDef.documentation)).append("\");\n");
            sb.append("    returnMap.put(\"--").append(entry.getKey()).append("\", ").append(entry.getKey()).append(");\n");
            if (!argDef.shortField.equals("")) {
                sb.append("    returnMap.put(\"-").append(argDef.shortField).append("\", ").append(entry.getKey()).append(");\n");
            }

        }

        sb.append("    return returnMap;\n");
        sb.append("  }\n\n");

        sb.append("  private static class Argument {\n");
        sb.append("    public String value;\n");
        sb.append("    public String name;\n");
        sb.append("    public String documentation;\n\n");
        sb.append("    public Argument(String documentation) {\n");
        sb.append("      this.documentation = documentation;\n");
        sb.append("    }\n\n");
        sb.append("    public Argument(String name, String documentation) {\n");
        sb.append("      this.name = name;\n");
        sb.append("      this.documentation = documentation;\n");
        sb.append("    }\n\n");
        sb.append("    public Argument(Argument copy) {\n");
        sb.append("      this.value = copy.value;\n");
        sb.append("      this.documentation = copy.documentation;\n");
        sb.append("    }\n");
        sb.append("  }\n\n");




        // For all possible arguments create a field for it
        // Now for each possible command, create a class and set the fields to the correct arguments
        // Many ways to do this, but for now lets just do this way

        // Create a class for each command
        // For the fields, if it is an optional field set the default to default.
        // If it is a required field, then make sure it is there, otherwise add to exception stack.
        for (Group group : groups) {
            for (Command command : group.commandList) {
                sb.append("  public static class ").append(command.name + group.capName + "Args").append(" {\n");
                for (Argument argument : command.argList) {
                    sb.append("    public String ").append(argument.name).append(";\n");
                }
                sb.append("    public ").append(command.name + group.capName+"Args(ArgumentObj argObj) {\n");
                for (Argument argument : command.argList) {
                    sb.append("      String tempVal = argObj.currentMap.get(\"").append(argument.name).append("\").value;\n");
                    sb.append("      if (tempVal.equals(\"\")) {\n");
                    if (argument.optional) {
                        sb.append("        ").append(argument.name).append(" = ").append(argument.defaultValue).append(";\n");
                    }
                    sb.append("      } else {\n");
                    sb.append("        this.").append(argument.name).append(" = tempVal;\n");
                    sb.append("      }\n");
                    sb.append("    }\n");
                    sb.append("  }\n");
                }
            }
        }
        sb.append("}");
    return sb.toString();
    }
}
