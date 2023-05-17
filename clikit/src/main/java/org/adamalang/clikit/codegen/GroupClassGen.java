package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Argument;
import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Common;
import org.adamalang.clikit.model.Group;

import java.util.Map;
import java.util.TreeMap;

public class GroupClassGen {
    public static Map<String, String> generate(Group[] groupList, String packageName) {
        Map<String, String> generatedMap = new TreeMap<>();
        for (Group group: groupList) {
            StringBuilder sb = new StringBuilder();
            sb.append("package " + packageName + ".commands;\n");
            sb.append("import com.fasterxml.jackson.databind.JsonNode;\n" +
                    "import com.fasterxml.jackson.databind.node.ObjectNode;\n" +
                    "import " + packageName + ".Config;\n" +
                    "import " + packageName + ".Util;\n" +
                    "import " + packageName + ".remote.Connection;\n" +
                    "import " + packageName + ".remote.WebSocketClient;\n" +
                    "import org.adamalang.common.Hashing;\n" +
                    "import org.adamalang.common.Json;\n" +
                    "import java.io.File;\n" +
                    "import java.nio.file.Files;\n\n");

            sb.append("public class ").append(group.capName + " { \n");
            sb.append("  public static void execute(Config config, String[] args) throws Exception{\n");
            sb.append("    if (args.length == 0) {\n");
            sb.append("      ").append(group.name + "Help();\n");
            sb.append("      return;\n");
            sb.append("    }\n");
            sb.append("    String command = Util.normalize(args[0]);\n");
            sb.append("    String[] next = Util.tail(args);\n");
            sb.append("    switch (command) {\n");
            for (Command command : group.commandList) {
                if (command.name.equals("help"))
                    continue;
                sb.append("      case \"").append(command.name + "\":\n");
                sb.append("        "+group.name+command.capName).append("(config, next);\n");
                sb.append("         return;\n");
            }
            sb.append("      case \"help\":\n");
            sb.append("        ").append(group.name).append("Help();\n");
            sb.append("        return;\n");
            sb.append("     }\n");
            sb.append("  }\n");
            for (Command command : group.commandList) {
                if (command.name.equals("help"))
                    continue;
                sb.append("  public static void ").append(group.name+command.capName).append("(Config config, String[] args) throws Exception {\n");
                // Need a better catch-all method, easy to implement
                if (!command.method.equals("self")) {
                    sb.append("    String identity = config.get_string(\"identity\", null);\n");
                    for (Argument argument : command.argList) {
                        StringBuilder fullLine  = new StringBuilder();
                        String[] changeStrings = new String[]{"","","","",""};
                        switch (argument.definition.type) {
                            case "file":
                                changeStrings[0] = "    File ";
                                changeStrings[1] = "new File(";
                                changeStrings[4] = ")";
                                break;
                            case "int":
                                changeStrings[0] = "    int ";
                                changeStrings[1] = "Integer.parseInt(";
                                changeStrings[4] = ")";
                                break;
                            default:
                                changeStrings[0] = "    String ";
                        }
                        if (argument.optional) {
                            changeStrings[2] = "extractWithDefault";
                            if (argument.defaultValue.equals("null")) {
                                changeStrings[3] = "null, ";
                            } else {
                                changeStrings[3] = "\""+argument.defaultValue+"\", ";
                            }

                        } else {
                            changeStrings[2] = "extractOrCrash";
                        }

                        fullLine.append(changeStrings[0]).append(argument.name).append(" = ").append(changeStrings[1])
                                .append("Util.").append(changeStrings[2]).append("(\"--").append(argument.name)
                                .append("\", \"-").append(argument.name.charAt(0)).append("\", ")
                                .append(changeStrings[3]).append("args").append(changeStrings[4]).append(");\n");
                        sb.append(fullLine);
                    }

                sb.append("    try (WebSocketClient client = new WebSocketClient(config)) {\n");
                sb.append("      try (Connection connection = client.open()) {\n");
                sb.append("        ObjectNode request = Json.newJsonObject();\n");
                sb.append("        request.put(\"method\", ").append("\"").append(group.name + "/" + command.name)
                        .append("\");\n");
                sb.append("        request.put(\"identity\", identity);\n");
                for (Argument argument : command.argList) {
                    if (command.output != null && command.output.equals(argument.name))
                        continue;
                    if (argument.defaultValue.equals("null")) {
                        sb.append("        if (").append(argument.name).append(" != null) {\n");
                        sb.append("          request.put(\"").append(argument.name).append("\", ")
                                .append(argument.name).append(");\n");
                        sb.append("        }\n");
                    } else {
                        sb.append("        request.put(\"").append(argument.name).append("\", ")
                                .append(argument.name).append(");\n");
                    }

                }

                String outputStr = "System.err.println(response.toPrettyString());\n";
                if (command.output != null) {
                    outputStr = "Files.writeString(" + command.output + ".toPath(), response.toPrettyString());\n";
                }
                switch(command.method) {
                    case "stream":
                        sb.append("        connection.stream(request, (cId, response) -> {\n");
                        sb.append("          ").append(outputStr);
                        sb.append("        });\n");
                        break;

                    default:
                        sb.append("        ObjectNode response = connection.execute(request);\n");
                        sb.append("        ").append(outputStr);

                }
                sb.append("      }\n");
                sb.append("    }\n");



            }
                sb.append("  }\n");
            }

            sb.append("  public static void ").append(group.name + "Help() throws Exception {\n");
            sb.append("    System.out.println(Util.prefix(\"").append(group.documentation).append("\", Util.ANSI.Green));\n");
            sb.append("    System.out.println();\n");
            sb.append("    System.out.println(Util.prefix(\"USAGE:\", Util.ANSI.Yellow));\n");
            sb.append("    System.out.println(\"    \" + Util.prefix(\"adama");
            sb.append(" "+group.name).append("\", Util.ANSI.Green) + \" \" + Util.prefix(\"[SUBCOMMAND]\", Util.ANSI.Magenta));\n");
            sb.append("    System.out.println();\n");
            sb.append("    System.out.println(Util.prefix(\"FLAGS:\", Util.ANSI.Yellow));\n");
            sb.append("    System.out.println(\"    \" + Util.prefix(\"--config\", Util.ANSI.Green) + \"          Supplies a config file path other than the default (~/.adama)\");\n");
            sb.append("    System.out.println();\n");
            sb.append("    System.out.println(Util.prefix(\"SUBCOMMANDS:\", Util.ANSI.Yellow));\n");
            for (Command command: group.commandList) {
                sb.append("    System.out.println(\"    \" + Util.prefix(\"");
                sb.append(Common.lJust(command.name, 18));
                sb.append("\", Util.ANSI.Green) + \"");
                sb.append(command.documentation);
                sb.append("\");\n");
            }
            sb.append("  }\n");
            sb.append("}");
            generatedMap.put(group.capName, sb.toString());

        }
        return generatedMap;


    }
}
