package org.adamalang.cli.router;


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
            group = "none";
            return;
        }
        if (args.length >= 1) {
            // Groups could be hashmap

            if (args[0] == "help, --help, -h") {
               group = "none";
               return;
            } else {
                Groups thisGroup = groups.get(args[0]);
                if (thisGroup) {
                    group = thisGroup;
                    argStart = 1;
                } else {
                    System.out.println("Command '" + args[0] + "' is not a valid command. See 'adama.jar --help'");
                    return;
                }

            }
        }
        if (args.length >= 2) {
            if (args[1] == "help, --help, -h") {
                command = "none";
                return;
            } else {
                // If group is command
                if (!group.commands.isEmpty()) {
                    Command thisCommand = group.command.get(args[1]);
                    if (thisCommand != null) {
                        command = thisCommand;
                        argStart = 2;
                    } else {
                        System.out.println("Command '" + args[1] + "' is not a valid command of '" + group + "'. See 'adama.jar " + group + " --help'");
                    }
                }
            }
        }

        HashMap<String, ArgumentItem> loop;
        if (command != null) {
            loop = command.arguments;
        } else {
            loop = group.arguments;
        }
//
//
//        // This only confirms if the arguments are in the hashmap.
//        for (int i = 2; i < args.length; i++) {
//            ArgumentItem current = loop.get(args[i]);
//            if (current != null) {
//                // If it is a flag, then go next , if it is type, then skip two.
//                arguments.put(args[i], current);
//            } else {
//                anyInvalid = true;
//                System.out.println("Argument '" + args[i] + "' is not a valid argument.");
//            }
//
//        }

        boolean anyInvalid = false;
        // Does not check for arguments that are not available...
        // Could be bad, could be good, let's see.
        outerloop:
        for (Map.Entry<String, ArgumentItem> entry : loop.entrySet()) {
            ArgumentItem val = entry.getValue();
            for (int i = argStart; i < args.length; i++) {
                if (val.name == args[i]) {
                    if (args.length < i+1) {
                        // If it is a flag, then go next , if it is type, then skip two.
                        val.value = args[++i];
                        arguments.put(args[i], val);
                        continue outerloop;
                    }
                }
            }
            if (val.optional) {
                val.value = val.defaultArg;
            } else {
                anyInvalid = true;
                System.out.println("Expected argument '" + val.name + "'");
            }
        }

        if (anyInvalid) {
            valid = false;
            System.out.println("See 'adama.jar " + (group != null ? group + " " : "") + (command != null ? command + " " : "") + "--help");
        }
    }
}