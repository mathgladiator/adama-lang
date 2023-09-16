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

import jdk.jshell.execution.Util;
import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Common;
import org.adamalang.clikit.model.Group;

import java.util.Locale;

public class HelpGen {
    /** Generates the help for each command and group **/
    public static String generate(Group[] groups, Command[] commands, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n");
        sb.append("import org.adamalang.cli.Util;\n");
        sb.append("public class Help {\n");
        sb.append("  public static void displayRootHelp() {\n");
        sb.append("    System.out.println(Util.prefix(\"Interacts with the Adama Platform\", Util.ANSI.Green));\n");
        sb.append("    System.out.println();\n");
        sb.append("    System.out.println(\"    \" + Util.prefix(\"adama\", Util.ANSI.Green) + \" \" + Util.prefix(\"[SUBCOMMAND]\", Util.ANSI.Magenta));\n");
        sb.append("    System.out.println();\n");
        sb.append("    System.out.println(\"    \" + Util.prefix(Util.justifyLeft(\"--config\", 15), Util.ANSI.Green) + \"Supplies a config file path other than the default (~/.adama)\");\n");
        sb.append("    System.out.println();\n");
        sb.append("    System.out.println(Util.prefix(\"SUBCOMMANDS:\", Util.ANSI.Yellow));\n");
        for (Group group : groups) {
            sb.append("    System.out.println(\"    \" + Util.prefix(Util.justifyLeft(\"").append(group.name).append("\", 15), Util.ANSI.Cyan) + \"").append(Common.escape(group.documentation)).append("\");\n");
        }
        for (Command command : commands) {
            sb.append("    System.out.println(\"    \" + Util.prefix(Util.justifyLeft(\"").append(command.name).append("\", 15), Util.ANSI.Green) + ").append("\"").append(Common.escape(command.documentation)).append("\");\n");
        }

        sb.append("  }\n");
        for (Group group : groups) {
            sb.append("  public static void display").append(group.capName).append("Help() {\n");
            sb.append("    System.out.println(Util.prefix(\"").append(Common.escape(group.documentation)).append("\", Util.ANSI.Green));\n");
            sb.append("    System.out.println();\n");
            sb.append("    System.out.println(Util.prefix(\"USAGE:\", Util.ANSI.Yellow));\n");
            sb.append("    System.out.println(\"    \" + Util.prefix(\"adama ").append(group.name).append("\", Util.ANSI.Green) + \" \" + Util.prefix(\"[").append(group.name.toUpperCase(Locale.ROOT)).append("SUBCOMMAND]\", Util.ANSI.Magenta));\n");
            sb.append("    System.out.println(Util.prefix(\"FLAGS:\", Util.ANSI.Yellow));\n");
            sb.append("    System.out.println(\"    \" + Util.prefix(Util.justifyLeft(\"--config\", 15), Util.ANSI.Green) + \"Supplies a config file path other than the default (~/.adama)\");\n");
            sb.append("    System.out.println();\n");
            sb.append("    System.out.println(Util.prefix(\"").append(group.name.toUpperCase(Locale.ROOT)).append("SUBCOMMAND:\", Util.ANSI.Yellow));\n");
            for (Command command : group.commandList) {
                sb.append("    System.out.println(\"    \" + Util.prefix(Util.justifyLeft(\"").append(command.name).append("\", 15), Util.ANSI.Green) + ").append("\"").append(Common.escape(command.documentation)).append("\");\n");
            }
            sb.append("  }\n");
        }
        sb.append("}\n");

        return sb.toString();
    }
}
