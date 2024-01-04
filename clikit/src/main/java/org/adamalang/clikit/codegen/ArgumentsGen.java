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
        sb.append("import org.adamalang.common.ANSI;\n");
        sb.append("import org.adamalang.common.ColorUtilTools;\n");
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
                sb.append("\t\t\t\treturnArgs.config = Config.fromArgs(args);\n");
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
                sb.append("\t\t\tSystem.out.println(ColorUtilTools.prefix(\"").append(Common.escape(command.documentation)).append("\", ANSI.Green));\n");
                sb.append("\t\t\tSystem.out.println(ColorUtilTools.prefixBold(\"USAGE:\", ANSI.Yellow));\n");
                sb.append("\t\t\tSystem.out.println(\"    \" + ColorUtilTools.prefix(\"adama ").append(group.name).append(" ").append(command.name).append("\", ANSI.Green)");
                if (command.argList.length > 0) {
                    sb.append("+ \" \" + ColorUtilTools.prefix(\"[FLAGS]\", ANSI.Magenta)");
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
                    sb.append("\t\t\tSystem.out.println(ColorUtilTools.prefixBold(\"FLAGS:\", ANSI.Yellow));\n");
                    for (Argument arg: requiredArgs) {
                        sb.append("\t\t\tSystem.out.println(\"    \" + ColorUtilTools.prefix(\"-").append(arg.definition.shortField).append(", --").append(arg.name).append("\", ANSI.Green) + \" \" + ColorUtilTools.prefix(\"<").append(arg.name).append(">\", ANSI.White) + \" : ").append(arg.definition.documentation).append("\");\n");
                    }
                }
                if (optionalArgs.size() > 0) {
                    sb.append("\t\t\tSystem.out.println(ColorUtilTools.prefixBold(\"OPTIONAL FLAGS:\", ANSI.Yellow));\n");
                    for (Argument arg: optionalArgs) {
                        sb.append("\t\t\tSystem.out.println(\"    \" + ColorUtilTools.prefix(\"-").append(arg.definition.shortField).append(", --").append(arg.name).append("\", ANSI.Green) + \" \" + ColorUtilTools.prefix(\"<").append(arg.name).append(">\", ANSI.White) + \" : ").append(arg.definition.documentation).append("\");\n");
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
            sb.append("\t\t\t\treturnArgs.config = Config.fromArgs(args);\n");
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
            sb.append("\t\t\tSystem.out.println(ColorUtilTools.prefix(\"").append(Common.escape(command.documentation)).append("\", ANSI.Green));\n");
            sb.append("\t\t\tSystem.out.println(ColorUtilTools.prefixBold(\"USAGE:\", ANSI.Yellow));\n");
            sb.append("\t\t\tSystem.out.println(\"    \" + ColorUtilTools.prefix(\"adama ").append(command.name).append("\", ANSI.Green)");
            if (command.argList.length > 0) {
                sb.append("+ \" \" + ColorUtilTools.prefix(\"[FLAGS]\", ANSI.Magenta)");
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
                sb.append("\t\t\tSystem.out.println(ColorUtilTools.prefixBold(\"FLAGS:\", ANSI.Yellow));\n");
                for (Argument arg: requiredArgs) {
                    sb.append("\t\t\tSystem.out.println(\"    \" + ColorUtilTools.prefix(\"-").append(arg.definition.shortField).append(", --").append(arg.name).append("\", ANSI.Green) + \" \" + ColorUtilTools.prefix(\"<").append(arg.name).append(">\", ANSI.White));\n");
                }
            }
            if (optionalArgs.size() > 0) {
                sb.append("\t\t\tSystem.out.println(ColorUtilTools.prefixBold(\"OPTIONAL FLAGS:\", ANSI.Yellow));\n");
                for (Argument arg: optionalArgs) {
                    sb.append("\t\t\tSystem.out.println(\"    \" + ColorUtilTools.prefix(\"-").append(arg.definition.shortField).append(", --").append(arg.name).append("\", ANSI.Green) + \" \" + ColorUtilTools.prefix(\"<").append(arg.name).append(">\", ANSI.White));\n");
                }
            }
            sb.append("\t\t}\n");
            sb.append("\t}\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}
