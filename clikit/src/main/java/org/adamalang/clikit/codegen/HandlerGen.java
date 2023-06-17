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

import java.util.HashMap;
import java.util.Map;

public class HandlerGen {
    /** Generates the handlers, which are the interfaces that are used to create commands and implement them **/
    public static Map<String, String> generate(Group[] groupList, String packageName) {
        HashMap<String, String> returnMap = new HashMap<>();
        for (Group group: groupList) {
            StringBuilder handler = new StringBuilder();
            String upperHandler = group.capName+"Handler";
            handler.append("package ").append(packageName).append(";\n\n");
            handler.append("import ").append(packageName).append(".Arguments.*;\n");
            handler.append("import org.adamalang.cli.runtime.Output.*;\n\n");
            handler.append("public interface " + upperHandler + " {\n");
            for (Command command : group.commandList) {
                String outputType;
                switch(command.output) {
                    case "json":
                        outputType = "JsonOrError";
                        break;
                    default:
                        outputType = "YesOrError";
                }
                handler.append("  void ").append(command.camel).append("(").append(group.capName).append(command.capName).append("Args args, ").append(outputType).append(" output) throws Exception;\n");
            }
            handler.append("}");
            returnMap.put(upperHandler+".java",handler.toString());
        }
        return returnMap;
    }
}
