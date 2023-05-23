package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Group;

import java.util.HashMap;
import java.util.Map;

public class HandlerGen {
    public static Map<String, String> generate(Group[] groupList, String packageName) {


        HashMap<String, String> returnMap = new HashMap<>();
        //import the correct handler
        for (Group group: groupList) {
            StringBuilder handler = new StringBuilder();
            String upperHandler = group.capName+"Handler";

            handler.append("package ").append(packageName).append(";\n\n");
            handler.append("import ").append(packageName).append(".ArgumentType.*;\n\n");
            handler.append("public interface " + upperHandler + " {\n");
            handler.append("  default int route(Argument args) {\n");
            handler.append("    if (args.command == null) {\n");
            handler.append("      return Help.displayHelp(\"").append(group.name).append("\");\n");
            handler.append("    }\n");
            // Using switch to route, could use a hashmap to route
            handler.append("    switch (args.command.name) {\n");
            for (Command command: group.commandList) {
                handler.append("      case \"").append(command.name).append("\":\n");
                // TODO: Figure out how to get output.
                handler.append("        return ").append(command.camel + group.capName).append("(new ").append(command.capName + group.capName + "Args(args), \"Output\");\n");
            }
            //Create args from argument class
            handler.append("      default:\n");
            handler.append("        Help.displayHelp(\"").append(group.name).append("\");\n");
            handler.append("        return 0;\n");
            handler.append("    }\n");
            handler.append("  }\n");

            for (Command command : group.commandList) {
                handler.append("  int ").append(command.camel).append(group.capName).append("(").append(command.capName).append(group.capName);
                handler.append("Args args, String output);\n");
            }
            handler.append("}");
            returnMap.put(upperHandler+".java",handler.toString());
        }

        return returnMap;
    }
}
