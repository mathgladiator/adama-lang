package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Group;

public class RootHandlerGen {
    /** Generates the handler, which will create the sub-command handlers and handle main commands **/
    public static String generate(Group[] groupList, Command[] commandList, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package " + packageName + ";\n\n");
        sb.append("import org.adamalang.cli.runtime.Output.*;\n");
        sb.append("import ").append(packageName).append(".Arguments.*;\n\n");
        sb.append("public interface RootHandler {\n");
        for (Group group : groupList) {
            sb.append("  ").append(group.capName).append("Handler make").append(group.capName).append("Handler();\n");
        }
        for (Command command : commandList) {
            sb.append("  void ").append(command.camel).append("(").append(command.capName).append("Args args, YesOrError output) throws Exception;\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
