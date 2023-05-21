package org.adamalang.cli.router;

public class Help {
    public static int displayHelp() {
        // Show all groups and the documentation for the groups.
        System.out.println("General Help");
       return 0;
    }

    public static int displayHelp(String group) {
        System.out.println("Help for " + group);
        return 0;
    }

    public static int displayHelp(String group, String command) {
        System.out.println("Help for " + group + " " + command);
        return 0;
    }


}
