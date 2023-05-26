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
            handler.append("import org.adamalang.cli.runtime.Argument;\n");
            handler.append("import org.adamalang.cli.runtime.Help;\n");
            handler.append("import org.adamalang.cli.runtime.Output;\n");
            handler.append("import ").append(packageName).append(".ArgumentType.*;\n\n");
            handler.append("public interface " + upperHandler + " {\n");
            handler.append("  default int route(Argument args) throws Exception {\n");
            handler.append("    if (args.command == null) {\n");
            handler.append("      return Help.displayHelp(\"").append(group.name).append("\");\n");
            handler.append("    }\n");
            // Using switch to route, could use a hashmap to route
            handler.append("    switch (args.command.name) {\n");
            for (Command command: group.commandList) {
                String argObjName = "";
                argObjName = "new " + command.capName + group.capName + "Args(args), ";

                String outputName = "";
                if (command.output != null) {
                    outputName = command.output;
                }
                handler.append("      case \"").append(command.name).append("\":\n");
                handler.append("        return ").append(command.camel + group.capName).append("(").append(argObjName).append("new ").append(outputName).append("Output(args));\n");
            }
            //Create args from argument class
            handler.append("      default:\n");
            handler.append("        Help.displayHelp(\"").append(group.name).append("\");\n");
            handler.append("        return 0;\n");
            handler.append("    }\n");
            handler.append("  }\n");

            for (Command command : group.commandList) {
                String argObjName = "";
                argObjName = command.capName + group.capName + "Args args, ";

                String outputName = "";
                if (command.output != null) {
                    outputName = command.output;
                }
                handler.append("  int ").append(command.camel).append(group.capName).append("(").append(argObjName).append(outputName).append("Output output) throws Exception;\n");
            }
            handler.append("}");
            returnMap.put(upperHandler+".java",handler.toString());
        }

        return returnMap;
    }
}
