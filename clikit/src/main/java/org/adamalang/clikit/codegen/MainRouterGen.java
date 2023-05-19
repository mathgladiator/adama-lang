package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Group;

public class MainRouterGen {
    public static String generate(Group[] groupList, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package " + packageName + ";\n\n");
        sb.append("public class MainRouter {\n");

        //Create a way to route to correct group.
        sb.append("  public static void route(String[] args) {\n");
        /* Given args, we should create an Arg object and get the correct command to run */
        sb.append("    ArgumentObj argObj = new ArgumentObj(args);\n");
        /* For now, we will just use a switch statement */
        sb.append("    switch (argObj.group) {\n");
        for (Group group : groupList) {
            sb.append("      case \"").append(group.name).append("\":\n");
            sb.append("        ").append(group.capName).append("Router.route(argObj);\n");
            sb.append("        break;\n");
        }

        sb.append("      default:\n");
        // Can just get all the groups and stuff... yea
        sb.append("        //displayHelp();\n");
        sb.append("    }\n");
        sb.append("  }\n");

        sb.append("}");


        return sb.toString();
    }
}
