/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
        sb.append("}\n");
        return sb.toString();
    }
}
