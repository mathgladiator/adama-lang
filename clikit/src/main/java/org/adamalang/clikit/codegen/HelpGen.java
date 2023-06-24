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
import org.adamalang.clikit.model.Common;
import org.adamalang.clikit.model.Group;

import java.util.Locale;

public class HelpGen {
    /** Generates the help for each command and group **/
    public static String generate(Group[] groups, String packageName) {
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
            sb.append("    System.out.println(\"    \" + Util.prefix(Util.justifyLeft(\"").append(group.name).append("\", 15), Util.ANSI.Green) + \"").append(Common.escape(group.documentation)).append("\");\n");
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
        sb.append("}");

        return sb.toString();
    }
}
