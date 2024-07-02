/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
      String sqlData = "SELECT `space`, SUM(delta_bytes) + SUM(asset_bytes) as `bytes` FROM `" + dataBase.databaseName + "`.`directory` WHERE `space` != 'ide' GROUP BY `space`";
      DataBase.walk(connection, add, sqlData);
      String sqlMetrics = "SELECT `space`, SUM(IF(`metrics` IS NULL, 0, LENGTH(`metrics`))) as `bytes` FROM `" + dataBase.databaseName + "`.`metrics` WHERE `space` != 'ide' GROUP BY `space`";
      DataBase.walk(connection, add, sqlMetrics);
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
