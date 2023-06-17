/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Group;

public class MainRouterGen {
    /** Used to generate the main router, which routes the user to a command based on command line arguments **/
    public static String generate(Group[] groupList, Command[] commandList, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package " + packageName + ";\n\n");
        sb.append("import org.adamalang.ErrorTable;\n");
        sb.append("import org.adamalang.cli.Util;\n");
        sb.append("import org.adamalang.cli.runtime.Output;\n");
        sb.append("import org.adamalang.cli.runtime.Output.*;\n");
        sb.append("import org.adamalang.common.ErrorCodeException;\n");
        sb.append("import static ").append(packageName).append(".Help.*;\n");
        sb.append("import static ").append(packageName).append(".Arguments.*;\n\n");
        sb.append("public class MainRouter {\n");
        sb.append("  public static int route(String[] args, RootHandler handler, Output output) {\n");
        sb.append("    try {\n");
        sb.append("      if (args.length == 0) {\n");
        sb.append("        displayRootHelp();\n");
        sb.append("        return 1;\n");
        sb.append("      }\n");
        sb.append("      switch (args[0]) {\n");
        for (Group group : groupList) {
            String handlerClass = group.capName + "Handler";
            String handlerObj = group.name + "Handler";
            String argsObj = group.name + "Args";
            sb.append("        case \"").append(group.name).append("\":\n");
            sb.append("          ").append(handlerClass).append(" ").append(handlerObj).append(" = handler.make").append(handlerClass).append("();\n");
            sb.append("          if (args.length == 1) {\n");
            sb.append("            display").append(group.capName).append("Help();\n");
            sb.append("            return 1;\n");
            sb.append("          }\n");
            sb.append("          switch (args[1]) {\n");
            for (Command command : group.commandList) {
                String argsClass = group.capName + command.capName + "Args";
                sb.append("            case \"").append(command.name).append("\": {\n");
                sb.append("              ").append(argsClass).append(" ").append(argsObj).append(" = ").append(argsClass).append(".from(args, 2);\n");
                sb.append("              if (").append(argsObj).append(" == null) {\n");
                sb.append("                ").append(argsClass).append(".help();\n");
                sb.append("                return 1;\n");
                sb.append("               }\n");
                String outputType;
                switch(command.output) {
                    case "json":
                        outputType = "JsonOrError";
                        break;
                    default:
                        outputType = "YesOrError";
                }
                sb.append("               ").append(outputType).append(" out = output.make").append(outputType).append("();\n");
                sb.append("               ").append(group.name).append("Handler.").append(command.camel).append("(").append(argsObj).append(", out);\n");
                sb.append("               return 0;\n");
                sb.append("            }\n");
            }
            sb.append("            case \"--help\":\n");
            sb.append("            case \"-h\":\n");
            sb.append("            case \"help\": {\n");
            sb.append("              display").append(group.capName).append("Help();\n");
            sb.append("              return 1;\n");
            sb.append("            }\n");
            sb.append("            default:\n");
            sb.append("              System.err.println(\"Invalid subcommand '\" + args[1] + \"' of command '").append(group.name).append("'\");\n");
            sb.append("              System.err.println(\"See 'adama ").append(group.name).append(" help' for a list of subcommands.\");\n");
            sb.append("              return 1;\n");
            sb.append("          }\n");
        }


        for (Command command : commandList) {
            String argsClass = command.capName + "Args";
            sb.append("          case \"").append(command.name).append("\": {\n");
            sb.append("            ").append(argsClass).append(" mainArgs = ").append(argsClass).append(".from(args, 1);\n");
            sb.append("            if (mainArgs == null) {\n");
            sb.append("              ").append(argsClass).append(".help();\n");
            sb.append("              return 1;\n");
            sb.append("             }\n");
            //TODO: Output should be grabbed from XML
            String outputType;
            switch(command.output) {
                case "json":
                    outputType = "JsonOrError";
                    break;
                default:
                    outputType = "YesOrError";
            }
            sb.append("             ").append(outputType).append(" out = output.make").append(outputType).append("();\n");
            sb.append("             handler.").append(command.camel).append("(mainArgs , out);\n");
            sb.append("             return 0;\n");
            sb.append("          }\n");
        }

        sb.append("          default:\n");
        sb.append("            System.err.println(\"Invalid command '\" + args[0] + \"'\");\n");
        sb.append("            System.err.println(\"See 'adama help' for a list of commands.\");\n");
        sb.append("            return 1;\n");
        sb.append("      }\n");
        sb.append("    } catch (Exception ex) {\n");
        sb.append("      if (ex instanceof ErrorCodeException) {\n");
        sb.append("        System.err.println(Util.prefix(\"[ERROR]\", Util.ANSI.Red));\n");
        sb.append("        System.err.println(\"#:\" + ((ErrorCodeException) ex).code);\n");
        sb.append("        System.err.println(\"Name:\" + ErrorTable.INSTANCE.names.get(((ErrorCodeException) ex).code));\n");
        sb.append("        System.err.println(\"Description:\" + ErrorTable.INSTANCE.descriptions.get(((ErrorCodeException) ex).code));\n");
        sb.append("      } else {\n");
        sb.append("        ex.printStackTrace();\n");
        sb.append("      }\n");
        sb.append("      return 1;\n");
        sb.append("    }\n");
        sb.append("  }\n");
        sb.append("}");
        return sb.toString();
    }
}
