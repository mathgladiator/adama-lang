/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
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
            String outputType;
            switch(command.output) {
                case "json":
                    outputType = "JsonOrError";
                    break;
                default:
                    outputType = "YesOrError";
            }
            sb.append("  void ").append(command.camel).append("(").append(command.capName).append("Args args, ").append(outputType).append(" output) throws Exception;\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
