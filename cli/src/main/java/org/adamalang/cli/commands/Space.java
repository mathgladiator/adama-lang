package org.adamalang.cli.commands;

import org.adamalang.cli.Config;
import org.adamalang.cli.Util;

public class Space {
    public static void execute(Config config, String[] args) {
        if (args.length == 0) {
            spaceHelp();
            return;
        }
        String command = Util.normalize(args[0]);
        String[] next = Util.tail(args);
        switch (command) {
            case "help":
                spaceHelp();
                return;
        }

    }

    public static void spaceHelp() {
        System.out.println("Adama organizes documents into spaces which share a common set of inflight");
        System.out.println("");
        System.out.println("USAGE:");
        System.out.println("    adama space [SPACESUBCOMMAND]");
        System.out.println("");
        System.out.println("SPACESUBCOMMAND:");
        System.out.println("    create            Creates a space");
        System.out.println("    list              List spaces");
        System.out.println("    deploy            Deploys a space");
        System.out.println("    download          Downloads a space deployment plan");
        System.out.println("    set-role          Set a role for another developer");
        System.out.println("    help              Show this helpful message");
    }
}
