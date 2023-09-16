/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
            handler.append("}\n");
            returnMap.put(upperHandler+".java",handler.toString());
        }
        return returnMap;
    }
}
