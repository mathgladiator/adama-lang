package org.adamalang.cli.router;


import org.adamalang.cli.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Argument {

    public boolean valid = true;
    public CliElement group;
    public CliElement command;
    private int argStart;

    public HashMap<String, ArgumentItem> arguments = new HashMap<>();

    public Argument(String[] args) {
        // Validate everything here...
        if (args.length  == 0) {
            group = null;
            return;
        }
        if (args.length >= 1) {
            // Groups could be hashmap

            if (Util.equalsMultiple(args[0], "help", "--help", "-h")) {
               group = null;
               return;
            } else {
                CliElement thisGroup = CliElement.Groups.get(args[0]);
                if (thisGroup != null) {
                    group = thisGroup;
                    argStart = 1;
                } else {
                    System.out.println("Command '" + args[0] + "' is not a valid command. See 'adama.jar --help'");
                    valid = false;
                    return;
                }

            }
        }

        if (args.length >= 2) {
            if (Util.equalsMultiple(args[1], "help", "--help", "-h")) {
                command = null;
                return;
            } else {
                // If group is command
                if (!group.Commands.isEmpty()) {
                    CliElement thisCommand = group.Commands.get(args[1]);
                    if (thisCommand != null) {
                        command = thisCommand;
                        argStart = 2;
                    } else {
                        System.out.println("Command '" + args[1] + "' is not a valid command of '" + group.name + "'. See 'adama.jar " + group.name + " --help'");
                        valid = false;
                    }
                }
            }
        }

        if (argStart == 2 && argStart < args.length && Util.equalsMultiple(args[argStart], "--help", "-h")) {
            Help.displayHelp(group.name, command.name);
            valid = false;
            return;
        }

        Map<String, ArgumentItem> loop;
        if (command != null) {
            loop = command.Arguments;
        } else {
            loop = group.Arguments;
        }



        boolean anyInvalid = false;
        // Does not check for arguments that are not available...
        // Could be bad, could be good, let's see.
        outerloop:
        for (Map.Entry<String, ArgumentItem> entry : loop.entrySet()) {
            ArgumentItem val = entry.getValue().copy();
            boolean empty = false;
            for (int i = argStart; i < args.length; i++) {
                String shortField = val.shortField != null ?  val.shortField : val.name;
                if (Util.equalsMultiple(args[i], val.name, shortField)) {
                    if (args.length > i+1) {
                        // If it is a flag, then go next , if it is type, then skip two.
                        val.value = args[i+1];
                        arguments.put(val.name, val);
                        i++;
                        continue outerloop;
                    } else {
                        empty = true;
                        break;
                    }
                }
            }
            if (empty) {
                anyInvalid = true;
                // Change to type soon...
                System.out.println("Expected string for argument '" + val.name + "'.");
            } else {
                if (val.optional) {
                    if (val.defaultArg.equals("null")) {
                        val.value = null;
                    } else {
                        val.value = val.defaultArg;
                    }
                } else {
                    anyInvalid = true;
                    System.out.println("Expected argument '" + val.name + "'");
                }
            }
        }
        if (anyInvalid) {
            valid = false;
            System.out.println("See 'adama.jar " + (group != null ? group.name + " " : "") + (command != null ? command.name + " " : "") + "--help'");
        }
    }
}