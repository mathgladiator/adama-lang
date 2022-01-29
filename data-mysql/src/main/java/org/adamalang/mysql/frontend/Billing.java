/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.frontend;

import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.frontend.data.MeteredWindowSummary;
import org.adamalang.mysql.frontend.data.MeteringSpaceSummary;
import org.adamalang.mysql.frontend.data.ResourcesPerPenny;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class Billing {

  public static void transcribeSummariesAndUpdateBalances(DataBase dataBase, int hour, HashMap<String, MeteringSpaceSummary> summaries, ResourcesPerPenny rates) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      for (Map.Entry<String, MeteringSpaceSummary> entry : summaries.entrySet()) {
        String sql = new StringBuilder("SELECT `id`,`latest_billing_hour` FROM `").append(dataBase.databaseName).append("`.`spaces` WHERE name=?").toString();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setString(1, entry.getKey());
          try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
              // we found it
              int spaceId = rs.getInt(1);
              int latestBillingHour = rs.getInt(2);
              if (latestBillingHour < hour) {
                MeteredWindowSummary summary = entry.getValue().summarize(rates);
                String sqlInsertLog = new StringBuilder().append("INSERT INTO `").append(dataBase.databaseName).append("`.`bills` (`space`, `hour`, `summary`, `pennies`) VALUES (?,?,?,?)").toString();
                try (PreparedStatement statementInsertLog = connection.prepareStatement(sqlInsertLog)) {
                  statementInsertLog.setInt(1, spaceId);
                  statementInsertLog.setInt(2, hour);
                  statementInsertLog.setString(3, summary.resources);
                  statementInsertLog.setInt(4, summary.pennies);
                  statementInsertLog.execute();
                }

                String sqlUpdateSpace = new StringBuilder().append("UPDATE `").append(dataBase.databaseName).append("`.`spaces` SET `balance`=`balance`-?, `latest_billing_hour`=? WHERE `id`=").append(spaceId).append(" LIMIT 1").toString();
                try (PreparedStatement statementUpdateSpace = connection.prepareStatement(sqlUpdateSpace)) {
                  statementUpdateSpace.setInt(1, summary.pennies);
                  statementUpdateSpace.setInt(2, hour);
                  statementUpdateSpace.execute();
                }
              }
            }
          }
        }
      }
    }
  }
}
