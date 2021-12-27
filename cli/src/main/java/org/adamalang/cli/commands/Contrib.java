package org.adamalang.cli.commands;

import org.adamalang.apikit.Tool;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.support.GenerateLanguageTests;

public class Contrib {
    public static void execute(Config config, String[] args) throws Exception {
        if (args.length == 0) {
            contribHelp();
            return;
        }
        String command = Util.normalize(args[0]);
        String[] next = Util.tail(args);
        switch (command) {
            case "generate":
                GenerateLanguageTests.generate(0, next);
                return;
            case "make-api":
                // TODO: needs a lot of love and testing
                Tool.build();
                return;
            case "help":
                contribHelp();
                return;
        }
    }

    public static void contribHelp() {
        System.out.println(Util.prefix("Adama development tools for contributors (i.e. people that contribute to the Adama Platform).", Util.ANSI.Green));
        System.out.println("");
        System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("adama contrib", Util.ANSI.Green) + " " + Util.prefix("[CONTRIBSUBCOMMAND]", Util.ANSI.Magenta));
        System.out.println("");
        System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
        System.out.println("");
        System.out.println(Util.prefix("CONTRIBSUBCOMMAND:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("generate", Util.ANSI.Green) + "          Generates the core test files from scripts.");
        System.out.println("    " + Util.prefix("make-api", Util.ANSI.Green) + "          Produces api files for SaaS and documentation for the WebSocket low level API.");
    }
}
