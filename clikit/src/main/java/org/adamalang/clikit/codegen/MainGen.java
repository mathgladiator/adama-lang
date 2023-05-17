package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Common;
import org.adamalang.clikit.model.Group;


public class MainGen {

    public static String generate(Group[] groupList, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package " + packageName + ".commands;\n\n");
        sb.append("import org.adamalang.ErrorTable;\n" +
                "import org.adamalang.cli.commands.*;\n" +
                "import org.adamalang.common.ErrorCodeException;\n" +
                "import com.fasterxml.jackson.databind.JsonNode;\n" +
                "import com.fasterxml.jackson.databind.node.ObjectNode;\n" +
                "import " + packageName + ".Config;\n" +
                "import " + packageName + ".Util;\n" +
                "import " + packageName + ".remote.Connection;\n" +
                "import " + packageName + ".remote.WebSocketClient;\n" +
                "import org.adamalang.common.Hashing;\n" +
                "import org.adamalang.common.Json;\n");
        sb.append("public class Main { \n");
        sb.append("  public static void main(String[] preFilteredArgs) throws Exception{\n");
        sb.append("    try {\n");
        sb.append("      Config config = new Config(preFilteredArgs);\n");
        sb.append("      if (preFilteredArgs.length == 0) {\n");
        sb.append("        rootHelp();\n");
        sb.append("        return;\n");
        sb.append("      }\n");
        sb.append("      String[] args = config.argsForTool;\n");
        sb.append("      String command = Util.normalize(args[0]);\n");
        sb.append("      String[] next = Util.tail(args);\n");
        sb.append("      switch (command) {\n");
        for (Group group : groupList) {
            sb.append("        case \"").append(group.name + "\":\n");
            sb.append("          "+group.capName).append(".execute(config, next);\n");
            sb.append("          return;\n");
        }
        sb.append("        case \"help\":\n");
        sb.append("          rootHelp();\n");
        sb.append("          return;\n");
        sb.append("      }\n");
        sb.append("    } catch (Exception ex) {\n");
        sb.append("      if (ex instanceof ErrorCodeException) {\n");
        sb.append("        System.err.println(Util.prefix(\"[ERROR]\", Util.ANSI.Red));\n");
        sb.append("        System.err.println(\"#:\" + ((ErrorCodeException) ex).code);\n");
        sb.append("        System.err.println(\"Name:\" + ErrorTable.INSTANCE.names.get(((ErrorCodeException) ex)" +
                ".code));\n");
        sb.append("        System.err.println(\"Description:\" + ErrorTable.INSTANCE.descriptions.get(((ErrorCodeException) ex).code));\n");
        sb.append("      } else {\n");
        sb.append("        ex.printStackTrace();\n");
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  }\n");
        sb.append("  public static void rootHelp() {\n");
        sb.append("    System.out.println(Util.prefix(\"Interacts with the Adama Platform\", Util.ANSI.Green));\n");
        sb.append("    System.out.println();\n");
        sb.append("    System.out.println(Util.prefix(\"USAGE:\", Util.ANSI.Yellow));\n");
        sb.append("    System.out.println(\"    \" + Util.prefix(\"adama\", Util.ANSI.Green) + \" \" + Util.prefix(\"[SUBCOMMAND]\", Util.ANSI.Magenta));\n");
        sb.append("    System.out.println();\n");
        sb.append("    System.out.println(Util.prefix(\"FLAGS:\", Util.ANSI.Yellow));\n");
        sb.append("    System.out.println(\"    \" + Util.prefix(\"--config\", Util.ANSI.Green) + \"          Supplies a config file path other than the default (~/.adama)\");\n");
        sb.append("    System.out.println();\n");
        sb.append("    System.out.println(Util.prefix(\"SUBCOMMANDS:\", Util.ANSI.Yellow));\n");
        for (Group group: groupList) {
            sb.append("    System.out.println(\"    \" + Util.prefix(\"");
            sb.append(Common.lJust(group.name, 18));
            sb.append("\", Util.ANSI.Green) + \"");
            sb.append(group.documentation);
            sb.append("\");\n");
        }
        sb.append("  }\n");
        sb.append("}");


        return sb.toString();
    }
}
