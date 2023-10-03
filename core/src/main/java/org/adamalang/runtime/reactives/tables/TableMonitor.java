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
package org.adamalang.runtime.reactives.tables;

import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.reactives.RxBase;
import org.adamalang.runtime.reactives.RxTable;

import java.util.ArrayList;
import java.util.TreeSet;

public abstract class TableMonitor extends RxBase {

  private class TableAndQueries implements TableSubscription, TableWatcher{
    private final RxTable<?> table;
    private boolean invalid;
    private boolean everything;
    private TreeSet<Integer> primaryKeys;
    private TreeSet<String> indexAndValues;

    public TableAndQueries(RxTable table) {
      this.table = table;
      this.invalid = false;
      this.everything = false;
      this.primaryKeys = null;
      this.indexAndValues = null;
    }

    @Override
    public boolean alive() {
      if (__parent != null) {
        return __parent.__isAlive();
      }
      return true;
    }

    // events to trigger
    @Override
    public void add(int primaryKey) {
      if (invalid) {
        return;
      }
      if (primaryKeys != null && primaryKeys.contains(primaryKey)) {
        __raiseDirty();
        invalid = true;
      }
      if (everything) {
        __raiseDirty();
        invalid = true;
      }
    }

    @Override
    public void change(int primaryKey) {
      if (invalid) {
        return;
      }
      if (primaryKeys != null && primaryKeys.contains(primaryKey)) {
        __raiseDirty();
        invalid = true;
      }
      if (everything) {
        __raiseDirty();
        invalid = true;
      }
    }

    @Override
    public void index(int primaryKey, String field, int value) {
      if (invalid) {
        return;
      }
      if (primaryKeys != null && primaryKeys.contains(primaryKey)) {
        __raiseDirty();
        invalid = true;
      }
      if (indexAndValues != null && indexAndValues.contains(field + "//" + value)) {
        __raiseDirty();
        invalid = true;
      }
      if (everything) {
        __raiseDirty();
        invalid = true;
      }
    }

    @Override
    public void remove(int primaryKey) {
      if (invalid) {
        return;
      }
      if (primaryKeys != null && primaryKeys.contains(primaryKey)) {
        __raiseDirty();
        invalid = true;
      }
      if (everything) {
        __raiseDirty();
        invalid = true;
      }
    }

    @Override
    public void entireTable(RxTable<?> table) {
      this.everything = true;
    }

    @Override
    public void index(RxTable<?> table, String field, int value) {
      if (indexAndValues == null) {
        indexAndValues = new TreeSet<>();
      }
      indexAndValues.add(field + "//" + value);
    }

    @Override
    public void primaryKey(RxTable<?> table, int id) {
      if (primaryKeys == null) {
        primaryKeys = new TreeSet<>();
      }
      primaryKeys.add(id);
    }

    public void watch() {
      this.invalid = false;
      this.everything = false;
      this.primaryKeys = null;
      this.indexAndValues = null;
      // table.watch(this);
    }

    public void unwatch() {
      // table.unwatch();
    }
  };

  private final ArrayList<TableAndQueries> tables;

  public TableMonitor(final RxParent __parent) {
    super(__parent);
    this.tables = new ArrayList<>();
  }

  public TableWatcher link(RxTable<?> tbl) {
    TableAndQueries watcher = new TableAndQueries(tbl);
    this.tables.add(watcher);
    return watcher;
  }

  public void watch() {
    for (TableAndQueries table : tables) {
      table.watch();
    }
  }

  public void unwatch() {
    for (TableAndQueries table : tables) {
      table.unwatch();
    }
  }
}
