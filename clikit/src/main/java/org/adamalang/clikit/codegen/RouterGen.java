package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Group;

import java.util.HashMap;
import java.util.Map;

public class RouterGen {
    public static Map<String, String> generate(Group[] groupList, String packageName) {


        HashMap<String, String> returnMap = new HashMap<>();
        //import the correct handler
        for (Group group: groupList) {
            StringBuilder router = new StringBuilder();
            String upperRouter = group.capName+"Router";

            router.append("package ").append(packageName).append(";\n\n");
            router.append("public interface " + upperRouter + " {\n");
            router.append("  default int route(String[] args) {\n");
            // Using switch to route, could use a hashmap to route

            //TODO: Use custom args
            router.append("    switch (args[1]) {\n");
            for (Command command: group.commandList) {
                router.append("      case \"").append(command.name).append("\":\n");

                // router.append("        ArgumentObj.").append(command.name + group.capName + "Args").append(" newArgs = new ArgumentObj.").append(command.name + group.capName + "Args(args);\n");
                // TODO: Figure out how to get output.
                router.append("        return ").append(command.name + group.capName + "(\"BRUH\", \"BRUH\");\n");
            }
            //Create args from argument class
            router.append("      default:\n");
            router.append("        return 0;\n");
            router.append("    }\n");
            router.append("  }\n");

            for (Command command : group.commandList) {
                router.append("  int ").append(command.name).append(group.capName).append("(String arguments, String output);\n");
            }
            router.append("}");
            returnMap.put(upperRouter+".java",router.toString());
        }

        return returnMap;
    }
}
