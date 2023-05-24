package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Group;

import java.util.HashMap;

public class TestGen {

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
            sb.append("  public void coverage {\n");
            for (Group group : groups) {
                sb.append("    NewMain.main(\"").append(group.name).append("\", \"--help\");\n");
                for (Command command : group.commandList) {
                    sb.append("    NewMain.main(\"").append(command.name).append("\", \"--help\");\n");
                }
            }
            for (Command command : mainCommands) {
                sb.append("    NewMain.main(\"").append(command.name).append("\", \"--help\");\n");
            }

            sb.append("  }\n");
            sb.append("}");
            returnTests.put("HelpCoverageTests.java", sb.toString());
        }

        return returnTests;
    }

}
