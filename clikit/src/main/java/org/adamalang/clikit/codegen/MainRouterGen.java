package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Group;

public class MainRouterGen {
    public static String generate(Group[] groupList, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package " + packageName + ";\n\n");
        sb.append("public interface RootHandler {\n");
        //Create a way to route to correct group.
        sb.append("  default int route(String[] args) {\n");
        /* Given args, we should create an Arg object and get the correct command to run */
        // sb.append("    ArgumentObj argObj = new ArgumentObj(args);\n");
        /* For now, we will just use a switch statement */
        sb.append("    switch (args[1]) {\n");
        for (Group group : groupList) {
            sb.append("      case \"").append(group.name).append("\":\n");
            sb.append("        ").append(group.capName).append("Router ").append(group.name).append("Router = createRouter();\n");
            sb.append("        return ").append(group.name).append("Router.route(args);\n");
        }

        sb.append("      default:\n");
        // Can just get all the groups and stuff... yea
        sb.append("        return displayHelp();\n");
        sb.append("    }\n");
        sb.append("  }\n");


        for (Group group : groupList) {
            //TODO: add for commands in main.
            sb.append("  ").append(group.capName).append("Router createRouter();\n");
        }

        sb.append("  default int displayHelp() {\n");
        sb.append("    return 0;\n");
        sb.append("  }\n");

        sb.append("}");




        return sb.toString();
    }
}
