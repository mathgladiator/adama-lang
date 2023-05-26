package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Group;

public class MainRouterGen {
    public static String generate(Group[] groupList, Command[] commandList, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package " + packageName + ";\n\n");
        sb.append("import org.adamalang.cli.runtime.Argument;\n");
        sb.append("import org.adamalang.cli.runtime.Help;\n");
        sb.append("import org.adamalang.cli.runtime.Output;\n");
        sb.append("import org.adamalang.ErrorTable;\n");
        sb.append("import org.adamalang.common.ErrorCodeException;\n");
        sb.append("import org.adamalang.cli.Util;\n");
        sb.append("import org.adamalang.cli.router.ArgumentType.*;\n\n");
        sb.append("public interface RootHandler {\n");
        //Create a way to route to correct group.
        sb.append("  default int route(String[] args) {\n");
        /* Given args, we should create an Arg object and get the correct command to run */
        // sb.append("    ArgumentObj argObj = new ArgumentObj(args);\n");
        /* For now, we will just use a switch statement */
        sb.append("    Argument arguments = new Argument(args);\n");
        sb.append("    if (!arguments.valid) {\n");
        sb.append("      return 0;\n");
        sb.append("    }\n");
        sb.append("    if (arguments.group == null) {\n");
        sb.append("      return Help.displayHelp();\n");
        sb.append("    }\n\n");
        sb.append("    try {\n");
        sb.append("      switch (arguments.group.name) {\n");
        for (Group group : groupList) {
            sb.append("        case \"").append(group.name).append("\":\n");
            sb.append("          ").append(group.capName).append("Handler ").append(group.name).append("Handler = create").append(group.capName).append("Handler();\n");
            sb.append("          return ").append(group.name).append("Handler.route(arguments);\n");
        }
        for (Command command : commandList) {
            String argObjName = "";
            argObjName = "new " + command.capName + "Args(arguments), ";

            String outputName = "";
            if (command.output != null) {
                outputName = command.output;
            }
            sb.append("        case \"").append(command.name).append("\":\n");
            sb.append("          return ").append(command.camel).append("(").append(argObjName).append("new ").append(outputName).append("Output(arguments));\n");
        }

        sb.append("        default:\n");
        sb.append("          return Help.displayHelp();\n");
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

        for (Group group : groupList) {
            sb.append("  ").append(group.capName).append("Handler create").append(group.capName).append("Handler();\n");
        }
        for (Command command : commandList) {
            String argObjName = "";
            argObjName = command.capName + "Args args, ";
            String outputName = "";
            if (command.output != null) {
                outputName = command.output;
            }
            sb.append("  int ").append(command.camel).append("(").append(argObjName).append(outputName).append("Output output) throws Exception;\n");
        }
        sb.append("}");




        return sb.toString();
    }
}
