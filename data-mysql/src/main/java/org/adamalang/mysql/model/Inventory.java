/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model;

import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.contracts.SQLConsumer;
import org.adamalang.mysql.data.SystemUsageInventoryRecord;

import java.util.HashMap;
import java.util.HashSet;

/** inventory the system */
public class Inventory {

  /** get the bytes per space */
  public static HashMap<String, Long> inventoryStorage(DataBase dataBase) throws Exception {
    return dataBase.transactSimple((connection) -> {
      HashMap<String, Long> bytes = new HashMap<>();
      SQLConsumer add = (rs) -> {
        String space = rs.getString(1);
        long controlStorage = rs.getLong(2);
        Long prior = bytes.get(space);
        if (prior == null) {
          bytes.put(space, controlStorage);
        } else {
          bytes.put(space, prior + controlStorage);
        }
      };
      String sqlData = "SELECT `space`, SUM(delta_bytes) + SUM(asset_bytes) + IF(`metrics` IS NULL, 0, LENGTH(`metrics`)) as `bytes` FROM `" + dataBase.databaseName + "`.`directory` WHERE `space` != 'ide' GROUP BY `space`";
      DataBase.walk(connection, add, sqlData);
      String sqlStaticAssets = "SELECT `key`,SUM(delta_bytes) + SUM(asset_bytes) as `bytes` FROM `" + dataBase.databaseName + "`.`directory` WHERE `space` = 'ide' GROUP BY `key`";
      DataBase.walk(connection, add, sqlStaticAssets);
      String sqlControl = "SELECT `name`, IF(`plan` IS NULL, 0, LENGTH(`plan`)) + IF(`rxhtml` IS NULL, 0, LENGTH(`rxhtml`)) as `bytes` FROM `" + dataBase.databaseName + "`.`spaces`;";
      DataBase.walk(connection, add, sqlControl);
      return bytes;
    });
  }

  /** get the system usage by user */
  public static HashMap<Integer, SystemUsageInventoryRecord> inventorySystemUsage(DataBase dataBase) throws Exception {
    return dataBase.transactSimple((connection) -> {
      HashMap<Integer, Integer> domainsByOwner = new HashMap<Integer, Integer>();
      HashMap<Integer, Integer> authoritiesByOwner = new HashMap<Integer, Integer>();
      HashSet<Integer> ownersWithData = new HashSet<>();
      String domainsSQL = "SELECT `owner`, count(`id`) as `count` from `" + dataBase.databaseName + "`.`domains` GROUP BY `owner`;";
      DataBase.walk(connection, (rs) -> {
        int owner = rs.getInt(1);
        ownersWithData.add(owner);
        domainsByOwner.put(owner, rs.getInt(2));
      }, domainsSQL);
      String authoritiesSQL = "SELECT `owner`, count(`id`) as `count` from `" + dataBase.databaseName + "`.`authorities` GROUP BY `owner`;";
      DataBase.walk(connection, (rs) -> {
        int owner = rs.getInt(1);
        ownersWithData.add(owner);
        authoritiesByOwner.put(owner, rs.getInt(2));
      }, authoritiesSQL);
      HashMap<Integer, SystemUsageInventoryRecord> usage = new HashMap<>();
      for (int owner : ownersWithData) {
        Integer domains = domainsByOwner.get(owner);
        Integer authorities = authoritiesByOwner.get(owner);
        usage.put(owner, new SystemUsageInventoryRecord(domains == null ? 0 : domains, authorities == null ? 0 : authorities));
      }
      return usage;
    });
  }
}
