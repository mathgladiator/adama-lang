package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Argument;
import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Group;

public class ArgumentTypeGen {
    public static String generate(Group[] groups, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("public class ArgumentType {\n");
        for (Group group: groups) {
            for (Command command : group.commandList) {
                //TODO: If command does not have arguments, can skip, but for now, ALL OF THEM DO
                sb.append("  public static class ").append(command.capName).append(group.capName).append("Args {\n");
                for (Argument argument : command.argList) {
                    //Argument could be a dash type, so be careful of that...
                    sb.append("    public String ").append(argument.name).append(";\n");
                }
                sb.append("    public ").append(command.capName).append(group.capName).append("Args(Argument arg) {\n");
                for (Argument argument : command.argList) {
                    sb.append("      this.").append(argument.name).append(" = ").append("arg.arguments.get(\"--").append(argument.name).append("\").value;\n");
                }
                sb.append("    }\n");
                sb.append("  }\n\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
