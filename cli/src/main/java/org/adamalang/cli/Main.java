package org.adamalang.cli;

import org.adamalang.cli.commands.Authority;
import org.adamalang.cli.commands.Init;
import org.adamalang.cli.commands.Space;

public class Main {

    public static void main(String[] preFilteredArgs) throws Exception {
        Config config = new Config(preFilteredArgs);
        if (preFilteredArgs.length == 0) {
            rootHelp();
            return;
        }
        String[] args = config.argsForTool;
        String command = Util.normalize(args[0]);
        String[] next = Util.tail(args);
        switch (command) {
            case "init":
                Init.execute(config);
                return;
            case "space":
                Space.execute(config, next);
                return;
            case "authority":
                Authority.execute(config, next);
                return;
            case "help":
                rootHelp();
                return;
        }
    }

    public static void rootHelp() {
        System.out.println(Util.prefix("Interacts with the Adama Platform", Util.ANSI.Green));
        System.out.println("");
        System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("adama", Util.ANSI.Green) + " " + Util.prefix("[SUBCOMMAND]", Util.ANSI.Magenta));
        System.out.println("");
        System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
        System.out.println("");
        System.out.println(Util.prefix("SUBCOMMANDS:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("init", Util.ANSI.Green) + "              Initializes the config with a valid token");
        System.out.println("    " + Util.prefix("space", Util.ANSI.Green) + "             Manages spaces");
        System.out.println("    " + Util.prefix("authority", Util.ANSI.Green) + "         Manage authorities");
    }
}
