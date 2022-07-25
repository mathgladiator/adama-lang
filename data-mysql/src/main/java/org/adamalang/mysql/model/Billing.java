/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Billing {

  public static void mergeStorageIntoSummaries(HashMap<String, MeteringSpaceSummary> summaries, HashMap<String, Long> inventory, HashMap<String, UnbilledResources> unbilled) {
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
    for (Map.Entry<String, UnbilledResources> entry : unbilled.entrySet()) {
      MeteringSpaceSummary summary = summaries.get(entry.getKey());
      if (summary != null) {
        summary.setUnbilled(entry.getValue());
      }
    }
  }

  public static long usageValueOfZeroIfNotPresentOrNull(ObjectNode node, String field) {
    JsonNode child = node.get(field);
    if (child == null || child.isNull()) {
      return 0;
    }
    return child.asLong();
  }

  public static ArrayList<BillingUsage> usageReport(DataBase dataBase, int spaceId, int limit) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      String sql = new StringBuilder("SELECT `hour`,`summary` FROM `").append(dataBase.databaseName).append("`.`bills` WHERE `space`=").append(spaceId).append(" ORDER BY `hour` DESC LIMIT ").append(limit).toString();
      ArrayList<BillingUsage> usage = new ArrayList<>();
      DataBase.walk(connection, (rs) -> {
        int hour = rs.getInt(1);
        ObjectNode resources = Json.parseJsonObject(rs.getString(2));
        long cpu = usageValueOfZeroIfNotPresentOrNull(resources, "cpu");
        long memory = usageValueOfZeroIfNotPresentOrNull(resources, "memory");
        int connections = (int) usageValueOfZeroIfNotPresentOrNull(resources, "connections");
        int documents = (int) usageValueOfZeroIfNotPresentOrNull(resources, "count");
        int messages = (int) usageValueOfZeroIfNotPresentOrNull(resources, "messages");
        long storageBytes = usageValueOfZeroIfNotPresentOrNull(resources, "storageBytes");
        long bandwidth = usageValueOfZeroIfNotPresentOrNull(resources, "bandwidth");
        long firstPartyServiceCalls = usageValueOfZeroIfNotPresentOrNull(resources, "first_party_service_calls");
        long thirdPartyServiceCalls = usageValueOfZeroIfNotPresentOrNull(resources, "third_party_service_calls");
        usage.add(new BillingUsage(hour, cpu, memory, connections, documents, messages, storageBytes, bandwidth, firstPartyServiceCalls, thirdPartyServiceCalls));
      }, sql);
      return usage;
    }
  }

  public static long transcribeSummariesAndUpdateBalances(DataBase dataBase, int hour, HashMap<String, MeteringSpaceSummary> summaries, ResourcesPerPenny rates) throws Exception {
    long pennies_billed = 0;
    try (Connection connection = dataBase.pool.getConnection()) {
      for (Map.Entry<String, MeteringSpaceSummary> entry : summaries.entrySet()) {
        String sql = new StringBuilder("SELECT `id`,`latest_billing_hour`,`owner` FROM `").append(dataBase.databaseName).append("`.`spaces` WHERE name=?").toString();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setString(1, entry.getKey());
          try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
              // we found it
              int spaceId = rs.getInt(1);
              int latestBillingHour = rs.getInt(2);
              int ownerId = rs.getInt(3);
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
               String sqlUpdateSpace = new StringBuilder().append("UPDATE `").append(dataBase.databaseName)
                   .append("`.`spaces` SET `latest_billing_hour`=?, `storage_bytes`=?," +
                    "`unbilled_storage_bytes_hours`=`unbilled_storage_bytes_hours`+?" +
                    ", `unbilled_bandwidth_hours`=`unbilled_bandwidth_hours`+?" +
                    ", `unbilled_first_party_service_calls`=`unbilled_first_party_service_calls`+?" +
                    ", `unbilled_third_party_service_calls`=`unbilled_third_party_service_calls`+?" + //
                    " WHERE `id`=").append(spaceId).append(" LIMIT 1").toString();

                try (PreparedStatement statementUpdateSpace = connection.prepareStatement(sqlUpdateSpace)) {
                  statementUpdateSpace.setInt(1, hour);
                  statementUpdateSpace.setLong(2, summary.storageBytes);
                  statementUpdateSpace.setLong(3, summary.changeUnbilled.storage);
                  statementUpdateSpace.setLong(4, summary.changeUnbilled.bandwidth);
                  statementUpdateSpace.setLong(5, summary.changeUnbilled.first_party_service_calls);
                  statementUpdateSpace.setLong(6, summary.changeUnbilled.third_party_service_calls);
                  statementUpdateSpace.execute();
                }
                String sqlUpdateDeveloperBalance = new StringBuilder().append("UPDATE `").append(dataBase.databaseName).append("`.`emails` SET `balance`=`balance`-").append(summary.pennies).append(" WHERE `id`=").append(ownerId).append(" LIMIT 1").toString();
                DataBase.execute(connection, sqlUpdateDeveloperBalance);
              }
            }
          }
        }
      }
    }
    return pennies_billed;
  }
}
