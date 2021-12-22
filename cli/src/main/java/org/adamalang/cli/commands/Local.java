package org.adamalang.cli.commands;

import org.adamalang.cli.Config;
import org.adamalang.cli.Util;

public class Local {
    public static void execute(Config config, String[] args) {
        if (args.length == 0) {
            authorityHelp();
            return;
        }
        String command = Util.normalize(args[0]);
        String[] next = Util.tail(args);
        switch (command) {
            case "help":
                authorityHelp();
                return;
        }
    }

    public static void authorityHelp() {
        System.out.println(Util.prefix("Local development tools.", Util.ANSI.Green));
        System.out.println("");
        System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("adama local", Util.ANSI.Green) + " " + Util.prefix("[LOCALSUBCOMMAND]", Util.ANSI.Magenta));
        System.out.println("");
        System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
        System.out.println("");
        System.out.println(Util.prefix("LOCALSUBCOMMAND:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("validate-plan", Util.ANSI.Green) + "     Validates a deployment plan (locally) for speed");
        System.out.println("    " + Util.prefix("compile-file", Util.ANSI.Green) + "      Compiles the adama file and shows any problems");
    }
}
