package org.adamalang.cli.commands;

import org.adamalang.cli.Config;
import org.adamalang.cli.Util;

public class Authority {
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
        System.out.println(Util.prefix("Manage how you and your users can interact with the Adama platform.", Util.ANSI.Green));
        System.out.println("");
        System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("adama authority", Util.ANSI.Green) + " " + Util.prefix("[AUTHORITYSUBCOMMAND]", Util.ANSI.Magenta));
        System.out.println("");
        System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
        System.out.println("");
        System.out.println(Util.prefix("AUTHORITYSUBCOMMAND:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("list", Util.ANSI.Green) + "              List authorities for developer");
        System.out.println("    " + Util.prefix("create", Util.ANSI.Green) + "            Creates a new authority");
        System.out.println("    " + Util.prefix("list-keys", Util.ANSI.Green) + "         List keys for an authority");
        System.out.println("    " + Util.prefix("add-key", Util.ANSI.Green) + "           Add a public key to an authority");
        System.out.println("    " + Util.prefix("revoke-key", Util.ANSI.Green) + "        Remove a key from an authority");
        System.out.println("    " + Util.prefix("manual", Util.ANSI.Green) + "            Show expanded help which illustrates how the above is used internally");
    }
}
