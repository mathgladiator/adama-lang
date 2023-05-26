package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Argument;
import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Group;

public class ArgumentTypeGen {
    public static String generate(Group[] groups, Command[] mainCommands, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import org.adamalang.cli.runtime.Argument;\n");
        sb.append("import org.adamalang.cli.Config;\n\n");
        sb.append("public class ArgumentType {\n");
        for (Group group: groups) {
            for (Command command : group.commandList) {
                sb.append("  public static class ").append(command.capName).append(group.capName).append("Args {\n");
                sb.append("    public Config config;\n");
                for (Argument argument : command.argList) {
                    sb.append("    public String ").append(argument.camel).append(";\n");
                }
                sb.append("    public ").append(command.capName).append(group.capName).append("Args(Argument arg) {\n");
                sb.append("    config = arg.config;\n");
                for (Argument argument : command.argList) {
                    sb.append("      this.").append(argument.camel).append(" = ").append("arg.arguments.get(\"--").append(argument.name).append("\").value;\n");
                }
                sb.append("    }\n");
                sb.append("  }\n\n");
            }
        }
        for (Command command : mainCommands) {
            sb.append("  public static class ").append(command.capName).append("Args {\n");
            sb.append("    public Config config;\n");
            for (Argument argument : command.argList) {
                sb.append("    public String ").append(argument.camel).append(";\n");
            }
            sb.append("    public ").append(command.capName).append("Args(Argument arg) {\n");
            sb.append("    config = arg.config;\n");
            for (Argument argument : command.argList) {

                sb.append("      this.").append(argument.camel).append(" = ").append("arg.arguments.get(\"--").append(argument.name).append("\").value;\n");
            }
            sb.append("    }\n");
            sb.append("  }\n\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
