/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.Argument;
import org.adamalang.clikit.model.Command;
import org.adamalang.clikit.model.Common;
import org.adamalang.clikit.model.Group;

import java.util.ArrayList;

public class ArgumentsGen {
    /**
     * Generates the argument class, which is a collection of classes used for each command.
     * Each class in the generated file represents args used in that command
     **/
    public static String generate(Group[] groups, Command[] mainCommands, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import org.adamalang.cli.Util;\n");
        sb.append("import org.adamalang.cli.Config;\n\n");
        sb.append("public class Arguments {\n");
        // Can have a superclass that grabs the config
        for (Group group : groups) {
            for (Command command : group.commandList) {
                String argClass = group.capName + command.capName + "Args";
                sb.append("\tpublic static class ").append(argClass).append(" {\n");
                sb.append("\t\tpublic Config config;\n");
                for (Argument argument : command.argList) {
                    // TODO: Change based on type
                    sb.append("\t\tpublic String ").append(argument.camel);
                    if (argument.optional) {
                        String optionalValue = argument.defaultValue.equals("null") ? argument.defaultValue : "\"" + argument.defaultValue + "\"";
                        sb.append(" = ").append(optionalValue);
                    }
                    sb.append(";\n");

                }
                sb.append("\t\tpublic static ").append(argClass).append(" from(String[] args, int start) {\n");
                sb.append("\t\t\t").append(argClass).append(" returnArgs = ").append("new ").append(argClass).append("();\n");
                sb.append("\t\t\ttry {\n");
                sb.append("\t\t\t\treturnArgs.config = new Config(args);\n");
                sb.append("\t\t\t} catch (Exception er) {\n");
                sb.append("\t\t\t\tSystem.out.println(\"Error creating default config file.\");\n");
                sb.append("\t\t\t}\n");
                boolean hasReq = false;
                for (Argument argument : command.argList) {
                    if (!argument.optional) {
                        hasReq = true;
                        break;
                    }
                }
                if (hasReq) {
                    sb.append("\t\t\tString[] missing = new String[]{");
                    for (Argument argument : command.argList) {
                        if (!argument.optional) {
                            sb.append("\"--").append(argument.name).append("\", ");
                        }
                    }
                    sb.append("};\n");
                }
                sb.append("\t\t\tfor (int k = start; k < args.length; k++) {\n");
                sb.append("\t\t\t\tswitch(args[k]) {\n");
                int reqPos = 0;
                for (Argument argument : command.argList) {
                    sb.append("\t\t\t\t\tcase \"-").append(argument.definition.shortField).append("\":\n");
                    sb.append("\t\t\t\t\tcase \"--").append(argument.name).append("\": {\n");
                    // TODO: Change based on type, and allow flags
                    sb.append("\t\t\t\t\t\tif (k+1 < args.length) {\n");
                    sb.append("\t\t\t\t\t\t\treturnArgs.").append(argument.camel).append(" = args[k+1];\n");
                    sb.append("\t\t\t\t\t\t\tk++;\n");
                    if (!argument.optional) {
                        sb.append("\t\t\t\t\t\t\tmissing[").append(reqPos).append("] = null;\n");
                        reqPos++;
                    }
                    sb.append("\t\t\t\t\t\t} else {\n");
                    sb.append("\t\t\t\t\t\t\tSystem.err.println(\"Expected value for argument '\" + args[k] + \"'\");\n");
                    sb.append("\t\t\t\t\t\t\treturn null;\n");
                    sb.append("\t\t\t\t\t\t}\n");
                    sb.append("\t\t\t\t\t\tbreak;\n");
                    sb.append("\t\t\t\t\t}\n");
                }
                // Hard coded and also repeated in all commands...
                sb.append("\t\t\t\t\t\tcase \"--help\":\n");
                sb.append("\t\t\t\t\t\tcase \"-h\":\n");
                sb.append("\t\t\t\t\t\tcase \"help\":\n");
                sb.append("\t\t\t\t\t\t\tif (k == start)\n");
                sb.append("\t\t\t\t\t\t\t\treturn null;\n");
                sb.append("\t\t\t\t\t\tcase \"--config\":\n");
                sb.append("\t\t\t\t\t\t\tk++;\n");
                sb.append("\t\t\t\t\t\tcase \"--json\":\n");
                sb.append("\t\t\t\t\t\tcase \"--no-color\":\n");
                sb.append("\t\t\t\t\t\t\tbreak;\n");
                sb.append("\t\t\t\t\t\tdefault:\n");
                sb.append("\t\t\t\t\t\t\tSystem.err.println(\"Unknown argument '\"").append(" + args[k] + ").append("\"'\");\n");
                sb.append("\t\t\t\t\t\t\treturn null;\n");
                sb.append("\t\t\t\t}\n");
                sb.append("\t\t\t}\n");
                if (hasReq) {
                    sb.append("\t\t\tboolean invalid = false;\n");
                    sb.append("\t\t\tfor (String misArg : missing) {\n");
                    sb.append("\t\t\t\tif (misArg != null) {\n");
                    sb.append("\t\t\t\t\tSystem.err.println(\"Expected argument '\" + misArg + \"'\");\n");
                    sb.append("\t\t\t\t\tinvalid = true;\n");
                    sb.append("\t\t\t\t}\n");
                    sb.append("\t\t\t}\n");
                    sb.append("\t\t\treturn (invalid ? null : returnArgs);\n");
                } else {
                    sb.append("\t\t\treturn returnArgs;\n");
                }
                sb.append("\t\t}\n");
                sb.append("\t\tpublic static void help() {\n");
                sb.append("\t\t\tSystem.out.println(Util.prefix(\"").append(Common.escape(command.documentation)).append("\", Util.ANSI.Green));\n");
                sb.append("\t\t\tSystem.out.println(Util.prefixBold(\"USAGE:\", Util.ANSI.Yellow));\n");
                sb.append("\t\t\tSystem.out.println(\"    \" + Util.prefix(\"adama ").append(group.name).append(" ").append(command.name).append("\", Util.ANSI.Green)");
                if (command.argList.length > 0) {
                    sb.append("+ \" \" + Util.prefix(\"[FLAGS]\", Util.ANSI.Magenta)");
                }
                sb.append(");\n");
                ArrayList<Argument> requiredArgs = new ArrayList<>();
                ArrayList<Argument> optionalArgs = new ArrayList<>();
                for (Argument arg : command.argList) {
                    if (arg.optional)
                        optionalArgs.add(arg);
                    else
                        requiredArgs.add(arg);
                }
                if (requiredArgs.size() > 0) {
                    sb.append("\t\t\tSystem.out.println(Util.prefixBold(\"FLAGS:\", Util.ANSI.Yellow));\n");
                    for (Argument arg: requiredArgs) {
                        sb.append("\t\t\tSystem.out.println(\"    \" + Util.prefix(\"-").append(arg.definition.shortField).append(", --").append(arg.name).append("\", Util.ANSI.Green) + \" \" + Util.prefix(\"<").append(arg.name).append(">\", Util.ANSI.White));\n");
                    }
                }
                if (optionalArgs.size() > 0) {
                    sb.append("\t\t\tSystem.out.println(Util.prefixBold(\"OPTIONAL FLAGS:\", Util.ANSI.Yellow));\n");
                    for (Argument arg: optionalArgs) {
                        sb.append("\t\t\tSystem.out.println(\"    \" + Util.prefix(\"-").append(arg.definition.shortField).append(", --").append(arg.name).append("\", Util.ANSI.Green) + \" \" + Util.prefix(\"<").append(arg.name).append(">\", Util.ANSI.White));\n");
                    }
                }
                sb.append("\t\t}\n");
                sb.append("\t}\n");
            }
        }
        for (Command command : mainCommands) {
            String argClass = command.capName + "Args";
            sb.append("\tpublic static class ").append(argClass).append(" {\n");
            sb.append("\t\tpublic Config config;\n");
            for (Argument argument : command.argList) {
                // TODO: Change based on type
                sb.append("\t\tpublic String ").append(argument.camel);
                if (argument.optional) {
                    String optionalValue = argument.defaultValue.equals("null") ? argument.defaultValue : "\"" + argument.defaultValue + "\"";
                    sb.append(" = ").append(optionalValue);
                }
                sb.append(";\n");

            }
            sb.append("\t\tpublic static ").append(argClass).append(" from(String[] args, int start) {\n");
            sb.append("\t\t\t").append(argClass).append(" returnArgs = ").append("new ").append(argClass).append("();\n");
            sb.append("\t\t\ttry {\n");
            sb.append("\t\t\t\treturnArgs.config = new Config(args);\n");
            sb.append("\t\t\t} catch (Exception er) {\n");
            sb.append("\t\t\t\tSystem.out.println(\"Error creating default config file.\");\n");
            sb.append("\t\t\t}\n");
            boolean hasReq = false;
            for (Argument argument : command.argList) {
                if (!argument.optional) {
                    hasReq = true;
                    break;
                }
            }
            if (hasReq) {
                sb.append("\t\t\tString[] missing = new String[]{");
                for (Argument argument : command.argList) {
                    if (!argument.optional) {
                        sb.append("\"--").append(argument.name).append("\", ");
                    }
                }
                sb.append("};\n");
            }
            sb.append("\t\t\tfor (int k = start; k < args.length; k++) {\n");
            sb.append("\t\t\t\tswitch(args[k]) {\n");
            int reqPos = 0;
            for (Argument argument : command.argList) {
                sb.append("\t\t\t\t\tcase \"-").append(argument.definition.shortField).append("\":\n");
                sb.append("\t\t\t\t\tcase \"--").append(argument.name).append("\": {\n");
                // TODO: Change based on type, and allow flags
                sb.append("\t\t\t\t\t\tif (k+1 < args.length) {\n");
                sb.append("\t\t\t\t\t\t\treturnArgs.").append(argument.camel).append(" = args[k+1];\n");
                sb.append("\t\t\t\t\t\t\tk++;\n");
                if (!argument.optional) {
                    sb.append("\t\t\t\t\t\t\tmissing[").append(reqPos).append("] = null;\n");
                    reqPos++;
                }
                sb.append("\t\t\t\t\t\t} else {\n");
                sb.append("\t\t\t\t\t\t\tSystem.err.println(\"Expected value for argument '\" + args[k] + \"'\");\n");
                sb.append("\t\t\t\t\t\t\treturn null;\n");
                sb.append("\t\t\t\t\t\t}\n");
                sb.append("\t\t\t\t\t\tbreak;\n");
                sb.append("\t\t\t\t\t}\n");
            }
            // Hard coded and also repeated in all commands...
            sb.append("\t\t\t\t\t\tcase \"--help\":\n");
            sb.append("\t\t\t\t\t\tcase \"-h\":\n");
            sb.append("\t\t\t\t\t\tcase \"help\":\n");
            sb.append("\t\t\t\t\t\t\tif (k == start)\n");
            sb.append("\t\t\t\t\t\t\t\treturn null;\n");
            sb.append("\t\t\t\t\t\tcase \"--config\":\n");
            sb.append("\t\t\t\t\t\t\tk++;\n");
            sb.append("\t\t\t\t\t\tcase \"--json\":\n");
            sb.append("\t\t\t\t\t\tcase \"--no-color\":\n");
            sb.append("\t\t\t\t\t\t\tbreak;\n");
            sb.append("\t\t\t\t\t\tdefault:\n");
            sb.append("\t\t\t\t\t\t\tSystem.err.println(\"Unknown argument '\"").append(" + args[k] + ").append("\"'\");\n");
            sb.append("\t\t\t\t\t\t\treturn null;\n");
            sb.append("\t\t\t\t}\n");
            sb.append("\t\t\t}\n");
            if (hasReq) {
                sb.append("\t\t\tboolean invalid = false;\n");
                sb.append("\t\t\tfor (String misArg : missing) {\n");
                sb.append("\t\t\t\tif (misArg != null) {\n");
                sb.append("\t\t\t\t\tSystem.err.println(\"Expected argument '\" + misArg + \"'\");\n");
                sb.append("\t\t\t\t\tinvalid = true;\n");
                sb.append("\t\t\t\t}\n");
                sb.append("\t\t\t}\n");
                sb.append("\t\t\treturn (invalid ? null : returnArgs);\n");
            } else {
                sb.append("\t\t\treturn returnArgs;\n");
            }
            sb.append("\t\t}\n");
            sb.append("\t\tpublic static void help() {\n");
            sb.append("\t\t\tSystem.out.println(Util.prefix(\"").append(Common.escape(command.documentation)).append("\", Util.ANSI.Green));\n");
            sb.append("\t\t\tSystem.out.println(Util.prefixBold(\"USAGE:\", Util.ANSI.Yellow));\n");
            sb.append("\t\t\tSystem.out.println(\"    \" + Util.prefix(\"adama ").append(command.name).append("\", Util.ANSI.Green)");
            if (command.argList.length > 0) {
                sb.append("+ \" \" + Util.prefix(\"[FLAGS]\", Util.ANSI.Magenta)");
            }
            sb.append(");\n");
            ArrayList<Argument> requiredArgs = new ArrayList<>();
            ArrayList<Argument> optionalArgs = new ArrayList<>();
            for (Argument arg : command.argList) {
                if (arg.optional)
                    optionalArgs.add(arg);
                else
                    requiredArgs.add(arg);
            }
            if (requiredArgs.size() > 0) {
                sb.append("\t\t\tSystem.out.println(Util.prefixBold(\"FLAGS:\", Util.ANSI.Yellow));\n");
                for (Argument arg: requiredArgs) {
                    sb.append("\t\t\tSystem.out.println(\"    \" + Util.prefix(\"-").append(arg.definition.shortField).append(", --").append(arg.name).append("\", Util.ANSI.Green) + \" \" + Util.prefix(\"<").append(arg.name).append(">\", Util.ANSI.White));\n");
                }
            }
            if (optionalArgs.size() > 0) {
                sb.append("\t\t\tSystem.out.println(Util.prefixBold(\"OPTIONAL FLAGS:\", Util.ANSI.Yellow));\n");
                for (Argument arg: optionalArgs) {
                    sb.append("\t\t\tSystem.out.println(\"    \" + Util.prefix(\"-").append(arg.definition.shortField).append(", --").append(arg.name).append("\", Util.ANSI.Green) + \" \" + Util.prefix(\"<").append(arg.name).append(">\", Util.ANSI.White));\n");
                }
            }
            sb.append("\t\t}\n");
            sb.append("\t}\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}
