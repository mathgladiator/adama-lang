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
            String upperHandler = group.capName+"Handler";
            String upperRouter = group.capName+"Router";
            String lowerHandler = group.name+"Handler";

            router.append("package ").append(packageName).append(";\n\n");
            router.append("import ").append(packageName).append(".handler." + upperHandler + ";\n");
            router.append("public class " + upperRouter + " {\n");
            router.append("  private "+upperHandler+ " " + lowerHandler+ ";\n");
            router.append("  public static void route(ArgumentObj args) {\n");
            // Using switch to route, could use a hashmap to route
            router.append("    switch (args.group) {\n");
            for (Command command: group.commandList) {
                router.append("      case \"").append(command.name).append("\":\n");
                router.append("        ArgumentObj.").append(command.name + group.capName + "Args").append(" newArgs = new ArgumentObj.").append(command.name + group.capName + "Args(args);\n");
                // TODO: Figure out how to get output.
                router.append("        ").append(lowerHandler + "." + command.name + group.capName + "(newArgs, output);\n");
                router.append("        break;\n");
            }
            //Create args from argument class
            router.append("      default:\n");
            router.append("    }\n");
            router.append("  }\n");
            router.append("}");
            returnMap.put(upperRouter+".java",router.toString());
        }

        return returnMap;
    }
}
