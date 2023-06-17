/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Util;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.DatabaseHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.cli.Config;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.contracts.MigrationStatus;

public class DatabaseHandlerImpl implements DatabaseHandler {
    @Override
    public void configure(Arguments.DatabaseConfigureArgs args, Output.YesOrError output) throws Exception {
        Config config = args.config;
        String _role = "";
        System.out.println();
        while (!("frontend".equals(_role) || "backend".equals(_role) || "deployed".equals(_role) || "any".equals(_role))) {
            System.out.println("Role may be 'frontend', 'backend', 'deployed', or 'any'");
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

        // TODO: validate by connecting to the database
        config.manipulate((node) -> {
            ObjectNode roleNode = node.putObject(role);
            roleNode.put("jdbc_url", "jdbc:mysql://" + host + ":" + port);
            roleNode.put("user", username);
            roleNode.put("password", password);
            roleNode.put("database_name", dbname);
        });
    }


    @Override
    public void install(Arguments.DatabaseInstallArgs args, Output.YesOrError output) throws Exception {
        new Installer(new DataBase(new DataBaseConfig(new ConfigObject(args.config.read())), new DataBaseMetrics(new NoOpMetricsFactory()))).install();
    }

    @Override
    public void migrate(Arguments.DatabaseMigrateArgs args, Output.YesOrError output) throws Exception {
        Config config = args.config;
        DataBase priorDB = new DataBase(new DataBaseConfig(new ConfigObject(config.read())), new DataBaseMetrics(new NoOpMetricsFactory()));
        ObjectNode newConfig = Json.newJsonObject();
        newConfig.set("db", config.read().get("nextdb"));
        DataBase nextDB = new DataBase(new DataBaseConfig(new ConfigObject(newConfig)), new DataBaseMetrics(new NoOpMetricsFactory()));
        Migrate.copy(priorDB, nextDB, name -> System.out.println("At:" + name));
    }
}
