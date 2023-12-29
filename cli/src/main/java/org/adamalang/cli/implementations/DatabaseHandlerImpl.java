/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.ANSI;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.DatabaseHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.mysql.model.Users;

public class DatabaseHandlerImpl implements DatabaseHandler {
  @Override
  public void configure(Arguments.DatabaseConfigureArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    String _role = "";
    System.out.println();
    while (!("frontend".equals(_role) || "backend".equals(_role) || "deployed".equals(_role) || "any".equals(_role))) {
      System.out.println("Role may be 'frontend', 'backend', 'deployed', or 'any'");
      System.out.print(Util.prefix("    Role:", ANSI.Yellow));
      _role = System.console().readLine();
    }
    String role = _role;

    System.out.println();
    System.out.print(Util.prefix("    Host:", ANSI.Yellow));
    String host = System.console().readLine();

    System.out.println();
    System.out.print(Util.prefix("    Port[3306]:", ANSI.Yellow));
    String portStr = System.console().readLine();
    int port = portStr.trim().equals("") ? 3306 : Integer.parseInt(portStr);

    System.out.println();
    String _username = "";
    while ("".equals(_username)) {
      System.out.print(Util.prefix("Username:", ANSI.Yellow));
      _username = System.console().readLine().trim();
    }
    String username = _username;

    System.out.println();
    System.out.print(Util.prefix("Password:", ANSI.Red));
    String password = new String(System.console().readPassword());

    System.out.println();
    System.out.print(Util.prefix("Database:", ANSI.Yellow));
    String dbname = System.console().readLine();

    // TODO: validate by connecting to the database
    config.manipulate((node) -> {
      ObjectNode roleNode = node.putObject(role);
      roleNode.put("jdbc_url", "jdbc:mysql://" + host + ":" + port);
      roleNode.put("user", username);
      roleNode.put("password", password);
      roleNode.put("database_name", dbname);
    });
    output.out();
  }

  @Override
  public void install(Arguments.DatabaseInstallArgs args, Output.YesOrError output) throws Exception {
    new Installer(new DataBase(new DataBaseConfig(new ConfigObject(args.config.read())), new DataBaseMetrics(new NoOpMetricsFactory()))).install();
    output.out();
  }

  @Override
  public void makeReserved(Arguments.DatabaseMakeReservedArgs args, Output.YesOrError output) throws Exception {
    DataBase db = new DataBase(new DataBaseConfig(new ConfigObject(args.config.read())), new DataBaseMetrics(new NoOpMetricsFactory()));
    int userId = Users.getUserId(db, args.email);
    try {
      Spaces.getSpaceInfo(db, args.space);
    } catch (ErrorCodeException ex) {
      if (ex.code == ErrorCodes.FRONTEND_SPACE_DOESNT_EXIST) {
        Spaces.createSpace(db, userId, args.space);
        return;
      }
      throw ex;
    }
  }

  @Override
  public void migrate(Arguments.DatabaseMigrateArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    DataBase priorDB = new DataBase(new DataBaseConfig(new ConfigObject(config.read())), new DataBaseMetrics(new NoOpMetricsFactory()));
    ObjectNode newConfig = Json.newJsonObject();
    newConfig.set("db", config.read().get("nextdb"));
    DataBase nextDB = new DataBase(new DataBaseConfig(new ConfigObject(newConfig)), new DataBaseMetrics(new NoOpMetricsFactory()));
    Migrate.copy(priorDB, nextDB, name -> System.out.println("At:" + name));
    output.out();
  }
}
