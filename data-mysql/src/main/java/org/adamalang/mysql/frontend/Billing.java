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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.json.JsonStreamWriter;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Billing {
  public static void recordBatch(DataBase dataBase, String target, String batch, long time) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      {
        String sql =
            new StringBuilder()
                .append("INSERT INTO `")
                .append(dataBase.databaseName)
                .append("`.`billing_batches` (`target`, `batch`, `created`) VALUES (?,?,?)")
                .toString();

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
          statement.setString(1, target);
          statement.setString(2, batch);
          statement.setString(3, DataBase.dateTimeOf(time));
          statement.execute();
        }
      }
    }
  }

  public static class SpaceSummary {
    private long cpuTicks;
    private long messages;

    private class PerTarget {
      private long count;
      private long memory;

      public PerTarget() {
        this.count = 0;
        this.memory = 0;
      }

      public void include(long count, long memory) {
        this.count = Math.max(this.count, count);
        this.memory = Math.max(this.memory, memory);
      }
    }

    private HashMap<String, PerTarget> targets;

    private SpaceSummary() {
      this.targets = new HashMap<>();
      this.cpuTicks = 0;
    }

    private void include(String target, JsonNode node) {
      PerTarget byTarget = targets.get(target);
      if (byTarget == null) {
        byTarget = new PerTarget();
        targets.put(target, byTarget);
      }
      cpuTicks += node.get("cpu").asLong();
      messages += node.get("messages").asLong();
      byTarget.include(node.get("count_p95").asLong(), node.get("memory_p95").asLong());
    }

    public String summarize() {
      JsonStreamWriter writer = new JsonStreamWriter();
      writer.beginObject();
      writer.writeObjectFieldIntro("cpu");
      writer.writeLong(cpuTicks);
      writer.writeObjectFieldIntro("messages");
      writer.writeLong(messages);
      long count = 0;
      long memory = 0;
      for (PerTarget target : targets.values()) {
        count += target.count;
        memory += target.memory;
      }
      writer.writeObjectFieldIntro("count");
      writer.writeLong(count);
      writer.writeObjectFieldIntro("memory");
      writer.writeLong(memory);
      writer.endObject();
      return writer.toString();
    }
  }

  public static HashMap<String, SpaceSummary> summarizeWindow(
      DataBase dataBase, long fromTime, long toTime) throws Exception {
    try (Connection connection = dataBase.pool.getConnection()) {
      {
        {
          DataBase.walk(connection, (rs) -> {
            System.err.println(rs.getString(2));
            System.err.println(rs.getString(1) + "/" + (new Date(fromTime).toString())+ "/" + (new Date(toTime).toString()));
          }, "SELECT `created`,`target` FROM `" + dataBase.databaseName + "`.`billing_batches`");
        }
        String sql =
            new StringBuilder()
                .append("SELECT `target`, `batch` FROM `")
                .append(dataBase.databaseName)
                .append("`.`billing_batches` WHERE ? <= `created` AND `created` < ?")
                .toString();
        HashMap<String, SpaceSummary> summary = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
          statement.setString(1, DataBase.dateTimeOf(fromTime));
          statement.setString(2, DataBase.dateTimeOf(toTime));
          try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
              String target = rs.getString(1);
              ObjectNode node = Json.parseJsonObject(rs.getString(2));
              long sampleTime = node.get("time").asLong();
              if (sampleTime < fromTime) {
                // TODO: WARN ON LATENESS
              }
              if (sampleTime > toTime) {
                // TODO: WARN ON TOO EARLY (clock drift issue)
              }
              ObjectNode spaces = (ObjectNode) node.get("spaces");
              Iterator<Map.Entry<String, JsonNode>> it = spaces.fields();
              while (it.hasNext()) {
                Map.Entry<String, JsonNode> sampleSpace = it.next();
                SpaceSummary spaceSum = summary.get(sampleSpace.getKey());
                if (spaceSum == null) {
                  spaceSum = new SpaceSummary();
                  summary.put(sampleSpace.getKey(), spaceSum);
                }
                spaceSum.include(target, sampleSpace.getValue());
              }
            }
          }
        }
        return summary;
      }
    }
  }
}
