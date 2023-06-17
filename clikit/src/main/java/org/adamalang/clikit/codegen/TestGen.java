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

public class TestGen {
    /** Generates the test cases for the help documentation of each group and command **/
    public static HashMap<String, String> generate(Group[] groups, Command[] mainCommands, String packageName) {
        HashMap<String, String> returnTests = new HashMap<>();
        // Adopting scope localization from apikit
        {
            StringBuilder sb = new StringBuilder();
            sb.append("package ").append(packageName).append(";\n\n");
            sb.append("import org.adamalang.cli.NewMain;\n");
            sb.append("import org.junit.Test;\n\n");
            sb.append("public class HelpCoverageTests {\n");
            sb.append("  @Test\n");
            sb.append("  public void coverage() {\n");
            for (Group group : groups) {
                sb.append("    NewMain.testMain(new String[]{\"").append(group.name).append("\", \"--help\"});\n");
                for (Command command : group.commandList) {
                    sb.append("    NewMain.testMain(new String[]{\"").append(group.name).append("\", \"").append(command.name).append("\", \"--help\"});\n");
                }
            }
            for (Command command : mainCommands) {
                sb.append("    NewMain.testMain(new String[]{\"").append(command.name).append("\", \"--help\"});\n");
            }
            sb.append("  }\n");
            sb.append("}");
            returnTests.put("HelpCoverageTests.java", sb.toString());
        }
        return returnTests;
    }
}
