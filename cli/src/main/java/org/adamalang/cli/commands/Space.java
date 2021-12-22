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
            case "create":
                // TODO: (1) validate next[0] exists and is a validate space name, (2) send the command along to the socket
                return;
            case "list":
                // TODO: (1) search for marker in next, (2) search for limit in next, (3) send the command along, (4) return beautiful results
                return;
            case "deploy":
                // TODO: (1) search for --file OR --plan, (2.a) for --file, build a dumb plan and compile the file for fast, (2.b) for --plan validate the plan json, (3) send the plan to socket
                return;
            case "download":
                // TODO: (1) validate next[0] exists and then go forth and fetch the plan and return a pretty printed version
                return;
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
