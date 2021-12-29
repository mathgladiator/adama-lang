/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.cli.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.BaseConfig;
import org.adamalang.mysql.backend.BackendDataServiceInstaller;
import org.adamalang.mysql.frontend.FrontendManagementInstaller;

public class Database {
    public static void execute(Config config, String[] args) throws Exception {
        if (args.length == 0) {
            databaseHelp();
            return;
        }
        String command = Util.normalize(args[0]);
        String[] next = Util.tail(args);
        switch (command) {
            case "configure": {
                databaseConfigure(config);
                return;
            }
            case "install-frontend": {
                DataBase dataBase = new DataBase(new BaseConfig(config.read().toString(), "frontend"));
                new FrontendManagementInstaller(dataBase).install();
                return;
            }
            case "install-backend": {
                DataBase dataBase = new DataBase(new BaseConfig(config.read().toString(), "backend"));
                new BackendDataServiceInstaller(dataBase).install();
                return;
            }
            case "help":
                databaseHelp();
                return;
        }
    }

    public static void databaseConfigure(Config config) throws Exception {
        String _role = "";
        System.out.println();
        while (!("frontend".equals(_role) || "backend".equals(_role) || "any".equals(_role))) {
            System.out.println("Role may be 'frontend', 'backend', or 'any'");
            System.out.print(Util.prefix("    Role:", Util.ANSI.Yellow));
            _role = System.console().readLine();
        }
        String role = _role;

        System.out.println();
        System.out.print(Util.prefix("    Host:", Util.ANSI.Yellow));
        String host = System.console().readLine();

        System.out.println();
        System.out.print(Util.prefix("    Port[3306]:", Util.ANSI.Yellow));
        String portStr = System.console().readLine();
        int port = portStr.trim().equals("") ? 3306 : Integer.parseInt(portStr);

        System.out.println();
        String _username = "";
        while ("".equals(_username)) {
            System.out.print(Util.prefix("Username:", Util.ANSI.Yellow));
            _username = System.console().readLine().trim();
        }
        String username = _username;

        System.out.println();
        System.out.print(Util.prefix("Password:", Util.ANSI.Red));
        String password = new String(System.console().readPassword());

        System.out.println();
        System.out.print(Util.prefix("Database:", Util.ANSI.Yellow));
        String dbname = System.console().readLine();

        // TODO: validate
        config.manipulate((node) -> {
            ObjectNode roleNode = node.putObject(role);
            roleNode.put("jdbc_url", "jdbc:mysql://" + host + ":" + port);
            roleNode.put("user", username);
            roleNode.put("password", password);
            roleNode.put("database_name", dbname);
        });
    }

    public static void databaseHelp() {
        System.out.println(Util.prefix("Database management for the Adama Platform", Util.ANSI.Green));
        System.out.println("");
        System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("adama database", Util.ANSI.Green) + " " + Util.prefix("[DATABASESUBCOMMAND]", Util.ANSI.Magenta));
        System.out.println("");
        System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
        System.out.println("");
        System.out.println(Util.prefix("DATABASESUBCOMMAND:", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("configure", Util.ANSI.Green) + "         Update the configuration " + Util.prefix("(interactive)", Util.ANSI.Yellow));
        System.out.println("    " + Util.prefix("install-frontend", Util.ANSI.Green) + "  Install the tables for usage by the front end WebSocket");
        System.out.println("    " + Util.prefix("install-backend", Util.ANSI.Green) + "   Install the tables for usage by the gRPC compute/storage Adama tier");
    }
}
