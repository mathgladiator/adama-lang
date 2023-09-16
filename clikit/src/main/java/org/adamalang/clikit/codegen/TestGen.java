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

public class TestGen {
    /** Generates the test cases for the help documentation of each group and command **/
    public static HashMap<String, String> generate(Group[] groups, Command[] mainCommands, String packageName) {
        HashMap<String, String> returnTests = new HashMap<>();
        // Adopting scope localization from apikit
        {
            StringBuilder sb = new StringBuilder();
            sb.append("package ").append(packageName).append(";\n\n");
            sb.append("import org.adamalang.cli.Main;\n");
            sb.append("import org.junit.Test;\n\n");
            sb.append("public class HelpCoverageTests {\n");
            sb.append("  @Test\n");
            sb.append("  public void coverage() {\n");
            for (Group group : groups) {
                sb.append("    Main.testMain(new String[]{\"").append(group.name).append("\", \"--help\"});\n");
                for (Command command : group.commandList) {
                    sb.append("    Main.testMain(new String[]{\"").append(group.name).append("\", \"").append(command.name).append("\", \"--help\"});\n");
                }
            }
            for (Command command : mainCommands) {
                sb.append("    Main.testMain(new String[]{\"").append(command.name).append("\", \"--help\"});\n");
            }
            sb.append("  }\n");
            sb.append("}\n");
            returnTests.put("HelpCoverageTests.java", sb.toString());
        }
        return returnTests;
    }
}
