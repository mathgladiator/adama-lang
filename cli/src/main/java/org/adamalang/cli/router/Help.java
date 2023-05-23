package org.adamalang.cli.router;

import org.adamalang.cli.Util;

import java.util.Locale;
import java.util.Map;

public class Help {
    public static int displayHelp() {
        // Show all groups and the documentation for the groups.

        System.out.println(Util.prefix("Interacts with the Adama Platform.\n", Util.ANSI.Green));
        System.out.println(Util.prefix("USAGE: ", Util.ANSI.Yellow));
        System.out.println(Util.prefix("    adama ", Util.ANSI.Green) + Util.prefix("[SUBCOMMAND]\n", Util.ANSI.Magenta));
        System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix(Util.lJust("--config",15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
        System.out.println(Util.prefix("\nSUBCOMMAND:", Util.ANSI.Yellow));
        for (Map.Entry<String, CliElement> entry : CliElement.Groups.entrySet()) {
            CliElement element = entry.getValue();
            System.out.println("    " + Util.prefix(Util.lJust(element.name,15), Util.ANSI.Green) + element.doc);
        }

       return 0;
    }

    public static int displayHelp(String group) {
        // If it is a group, and not a command!
        CliElement groupElem = CliElement.Groups.get(group);
        System.out.println(Util.prefix(groupElem.doc + "\n", Util.ANSI.Green));
        System.out.println(Util.prefix("USAGE: ", Util.ANSI.Yellow));
        System.out.println(Util.prefix("    adama " + group.toLowerCase(Locale.ROOT), Util.ANSI.Green) + Util.prefix(" [" + group.toUpperCase(Locale.ROOT) + "SUBCOMMAND]\n", Util.ANSI.Magenta));
        System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix(Util.lJust("--config",15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
        System.out.println(Util.prefix("\n" + group.toUpperCase(Locale.ROOT) + "SUBCOMMAND:", Util.ANSI.Yellow));
        for (Map.Entry<String, CliElement> entry : groupElem.Commands.entrySet()) {
            CliElement element = entry.getValue();
            System.out.println("    " + Util.prefix(Util.lJust(element.name,15), Util.ANSI.Green) + element.doc);
        }
        return 0;
    }

    public static int displayHelp(String group, String command) {
        // Always a command... for now
        CliElement commandElem = CliElement.Groups.get(group).Commands.get(command);
        System.out.println(Util.prefix(commandElem.doc + "\n", Util.ANSI.Green));
        System.out.println(Util.prefix("USAGE: ", Util.ANSI.Yellow));
        System.out.println(Util.prefix("    adama " + group.toLowerCase(Locale.ROOT) + " " + command.toLowerCase(Locale.ROOT), Util.ANSI.Green) + Util.prefix(" [FLAGS]\n", Util.ANSI.Magenta));
        System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix(Util.lJust("--config",15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
        for (Map.Entry<String, ArgumentItem> entry : commandElem.Arguments.entrySet()) {
            //TODO: Show the alternative as well...
            //TODO: Specify if it is optional.
            ArgumentItem item = entry.getValue();
            System.out.println("    " + Util.prefix(Util.lJust(entry.getKey(), 15), Util.ANSI.Green) + item.documentation);
        }
        return 0;
    }


}
