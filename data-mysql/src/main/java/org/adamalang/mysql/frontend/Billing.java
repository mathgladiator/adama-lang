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
import org.adamalang.mysql.backend.BackendOperations;
import org.adamalang.mysql.frontend.data.MeteredWindowSummary;
import org.adamalang.mysql.frontend.data.MeteringSpaceSummary;
import org.adamalang.mysql.frontend.data.ResourcesPerPenny;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class Billing {

  public static void mergeStorageIntoSummaries(HashMap<String, MeteringSpaceSummary> summaries, HashMap<String, Long> inventory, HashMap<String, Long> unbilled) {
    for (Map.Entry<String, Long> entry : inventory.entrySet()) {
      if (entry.getValue() > 0) {
        MeteringSpaceSummary summary = summaries.get(entry.getKey());
        if (summary == null) {
          summary = new MeteringSpaceSummary();
          summaries.put(entry.getKey(), summary);
        }
        summary.setStorageBytes(entry.getValue());
      }
    }
    for (Map.Entry<String, Long> entry : unbilled.entrySet()) {
      MeteringSpaceSummary summary = summaries.get(entry.getKey());
      if (summary != null) {
        summary.setUnbilledStorageByteHours(entry.getValue());
      }
    }
  }

  public static long transcribeSummariesAndUpdateBalances(DataBase dataBase, int hour, HashMap<String, MeteringSpaceSummary> summaries, ResourcesPerPenny rates) throws Exception {
    long pennies_billed = 0;
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
                  pennies_billed += summary.pennies;
                }
                String sqlUpdateSpace = new StringBuilder().append("UPDATE `").append(dataBase.databaseName).append("`.`spaces` SET `balance`=`balance`-?, `latest_billing_hour`=?, `storage_bytes`=?, `unbilled_storage_bytes_hours`=`unbilled_storage_bytes_hours`+? WHERE `id`=").append(spaceId).append(" LIMIT 1").toString();
                try (PreparedStatement statementUpdateSpace = connection.prepareStatement(sqlUpdateSpace)) {
                  statementUpdateSpace.setInt(1, summary.pennies);
                  statementUpdateSpace.setInt(2, hour);
                  statementUpdateSpace.setLong(3, summary.storageBytes);
                  statementUpdateSpace.setLong(4, summary.changeUnbilledStorageByteHours);
                  statementUpdateSpace.execute();
                }
              }
            }
          }
        }
      }
    }
    return pennies_billed;
  }
}
