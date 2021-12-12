package org.adamalang.cli;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0 || "help".equals(args[0])) {
            rootHelp();
            return;
        }
    }

    public static void rootHelp() {
        System.out.println("Interacts with the Adama Platform");
        System.out.println("");
        System.out.println("USAGE:");
        System.out.println("    adama [SUBCOMMAND]");
        System.out.println("");
        System.out.println("FLAGS:");
        System.out.println("    -h, --help        Prints helpful information within a subcommand");
        System.out.println("    -c, --config      Sets the location for the configuration file (default: ~/.adama.goat)");
        System.out.println("    -V, --version     Prints the version information");
        System.out.println("");
        System.out.println("SUBCOMMANDS:");
        System.out.println("    init              Initializes the config with a valid token");
        System.out.println("    space             Manages spaces");
        System.out.println("    authority         Manage authorities");
    }

    public static void spaceHelp() {
        System.out.println("Adama organizes documents into spaces which share a common set of inflight");
        System.out.println("");
        System.out.println("USAGE:");
        System.out.println("    adama space [SPACESUBCOMMAND]");
        System.out.println("");
        System.out.println("FLAGS:");
        System.out.println("    -h, --help        Prints helpful information within a subcommand");
        System.out.println("    -c, --config      Sets the location for the configuration file (default: ~/.adama.goat)");
        System.out.println("    -V, --version     Prints the version information");
        System.out.println("");
        System.out.println("SPACESUBCOMMAND:");
        System.out.println("    create            Creates a space");
        System.out.println("    list              List spaces");
        System.out.println("    deploy            Deploys a space");
        System.out.println("    download          Downloads a space deployment plan");
        System.out.println("    set-role          Set a role for another developer");
    }


    public static void authorityHelp() {
        System.out.println("Interacts with the Adama Platform");
        System.out.println("");
        System.out.println("USAGE:");
        System.out.println("    adama authority [AUTHORITYSUBCOMMAND]");
        System.out.println("");
        System.out.println("FLAGS:");
        System.out.println("    -h, --help        Prints helpful information within a subcommand");
        System.out.println("    -c, --config      Sets the location for the configuration file (default: ~/.adama.goat)");
        System.out.println("    -V, --version     Prints the version information");
        System.out.println("");
        System.out.println("AUTHORITYSUBCOMMAND:");
        System.out.println("    create            Creates a new authority");
        System.out.println("    list              List authorities for developer");
        System.out.println("    list-keys         List keys for an authority");
        System.out.println("    add-key           Add a public key to an authority");
        System.out.println("    revoke-key        Remove a key from an authority");
    }
}
