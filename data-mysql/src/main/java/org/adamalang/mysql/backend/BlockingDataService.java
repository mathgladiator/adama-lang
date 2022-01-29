/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.backend;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.json.JsonAlgebra;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/** Implements the DataService while blocking the caller's thread */
public class BlockingDataService implements DataService {
  private final BackendMetrics metrics;
  private final DataBase dataBase;
  private final SimpleDateFormat dateFormat;

  public BlockingDataService(final BackendMetrics metrics, final DataBase dataBase) {
    this.metrics = metrics;
    this.dataBase = dataBase;
    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    dataBase.transact((connection) -> {
      // look up the index to get the id
      LookupResult lookup = lookup(connection, key);
      String walkRedoSQL = new StringBuilder("SELECT `redo` FROM `").append(dataBase.databaseName).append("`.`deltas` WHERE `parent`=").append(lookup.id).append(" ORDER BY `seq_begin`").toString();
      AutoMorphicAccumulator<String> merge = JsonAlgebra.mergeAccumulator();
      AtomicInteger reads = new AtomicInteger(0);
      DataBase.walk(connection, (rs) -> {
        metrics.read_get.run();
        reads.incrementAndGet();
        merge.next(rs.getString(1));
      }, walkRedoSQL);
      return new LocalDocumentChange(merge.finish(), reads.get());
    }, callback, ErrorCodes.GET_FAILURE);
  }

  public LookupResult lookup(Connection connection, Key key) throws SQLException, ErrorCodeException {
    metrics.lookup.run();
    PreparedStatement statement = connection.prepareStatement(new StringBuilder("SELECT `id`, `head_seq` FROM `").append(dataBase.databaseName).append("`.`index` WHERE `space`=? AND `key`=? LIMIT 1").toString());
    try {
      statement.setString(1, key.space);
      statement.setString(2, key.key);
      ResultSet rs = statement.executeQuery();
      try {
        if (rs.next()) {
          int id = rs.getInt(1);
          int head_seq = rs.getInt(2);
          return new LookupResult(id, head_seq);
        } else {
          throw new ErrorCodeException(ErrorCodes.UNIVERSAL_LOOKUP_FAILED);
        }
      } finally {
        rs.close();
      }
    } finally {
      statement.close();
    }
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    dataBase.transact((connection) -> {
      // build the sql into insert
      String insertIndexSQL = new StringBuilder() //
          .append("INSERT INTO `").append(dataBase.databaseName).append("`.`index` (") //
          .append("`space`, `key`, `head_seq`, `invalidate`, `when`, `delta_bytes`, `asset_bytes`) VALUES (?, ?, ") //
          .append("'").append(patch.seqEnd).append("', ") //
          .append("'").append(patch.requiresFutureInvalidation ? "1" : "0").append("', ") //
          .append("'").append(whenOf(patch)).append("', 0, 0)") //
          .toString();
      metrics.lookup_change.run();

      // execute the insert
      PreparedStatement statementInsertIndex = connection.prepareStatement(insertIndexSQL, Statement.RETURN_GENERATED_KEYS);
      try {
        statementInsertIndex.setString(1, key.space);
        statementInsertIndex.setString(2, key.key);
        statementInsertIndex.execute();

        // insert the delta
        int parent = DataBase.getInsertId(statementInsertIndex);
        insertDelta(connection, parent, patch, metrics.write_init);
      } finally {
        statementInsertIndex.close();
      }

      return null;
    }, callback, ErrorCodes.UNIVERSAL_INITIALIZE_FAILURE);
  }

  private String whenOf(RemoteDocumentUpdate patch) {
    return dateFormat.format(new Date(System.currentTimeMillis() + (patch.requiresFutureInvalidation ? patch.whenToInvalidateMilliseconds : 0)));
  }

  /** internal: insert a delta */
  private void insertDelta(Connection connection, int parent, RemoteDocumentUpdate patch, Runnable counter) throws SQLException {
    String insertDeltaSQL = new StringBuilder() //
        .append("INSERT INTO `").append(dataBase.databaseName).append("`.`deltas` (") //
        .append("`parent`, `seq_begin`, `seq_end`, `who_agent`, `who_authority`, `request`, `redo`, `undo`, `history_ptr`) VALUES (") //
        .append(parent).append(", '").append(patch.seqBegin).append("', '").append(patch.seqEnd).append("', ") //
        .append("?, ?, ?, ?, ?, '')") //
        .toString();
    PreparedStatement statement = connection.prepareStatement(insertDeltaSQL);
    counter.run();
    try {
      if (patch.who != null) {
        statement.setString(1, patch.who.agent);
        statement.setString(2, patch.who.authority);
      } else {
        statement.setString(1, "?");
        statement.setString(2, "adama");
      }
      statement.setString(3, patch.request);
      statement.setString(4, patch.redo);
      statement.setString(5, patch.undo);
      statement.execute();
    } finally {
      statement.close();
    }
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    dataBase.transact((connection) -> {
      RemoteDocumentUpdate first = patches[0];
      RemoteDocumentUpdate last = patches[patches.length - 1];

      // read the index
      LookupResult lookup = lookup(connection, key);
      if (lookup.head_seq + 1 != first.seqBegin) {
        throw new ErrorCodeException(ErrorCodes.UNIVERSAL_PATCH_FAILURE_HEAD_SEQ_OFF);
      }

      long deltaBytesGain = 0;
      long assetBytesGain = 0;
      for (RemoteDocumentUpdate patch : patches) {
        deltaBytesGain += patch.redo.length() + patch.undo.length() + patch.request.length();
        assetBytesGain += patch.assetBytes;
      }

      // update the index
      String updateIndexSQL = new StringBuilder() //
          .append("UPDATE `").append(dataBase.databaseName).append("`.`index` ") //
          .append("SET `head_seq`=").append(last.seqEnd) //
          .append(", `invalidate`=").append(last.requiresFutureInvalidation ? 1 : 0) //
          .append(", `when`='").append(whenOf(last)) //
          .append("', `delta_bytes`=`delta_bytes`+").append(deltaBytesGain) //
          .append(", `asset_bytes`=`asset_bytes`+").append(assetBytesGain) //
          .append(" WHERE `id`=").append(lookup.id).toString();
      DataBase.execute(connection, updateIndexSQL);
      metrics.lookup_change.run();

      // insert delta
      for (RemoteDocumentUpdate patch : patches) {
        insertDelta(connection, lookup.id, patch, metrics.write_patch);
      }
      return null;
    }, callback, ErrorCodes.PATCH_FAILURE);
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    dataBase.transact((connection) -> {
      // look up the index to get the id
      LookupResult lookup = lookup(connection, key);

      if (method == ComputeMethod.HeadPatch) {
        String walkUndoSQL = new StringBuilder("SELECT `redo` FROM `") //
            .append(dataBase.databaseName).append("`.`deltas` WHERE `parent`=").append(lookup.id) //
            .append(" AND `seq_begin` > ").append(seq) //
            .append(" ORDER BY `seq_begin` DESC").toString();
        AutoMorphicAccumulator<String> redo = JsonAlgebra.mergeAccumulator();
        AtomicInteger reads = new AtomicInteger(0);
        DataBase.walk(connection, (rs) -> {
          reads.incrementAndGet();
          metrics.read_head_patch.run();
          redo.next(rs.getString(1));
        }, walkUndoSQL);
        if (redo.empty()) {
          throw new ErrorCodeException(ErrorCodes.COMPUTE_EMPTY_PATCH);
        }
        return new LocalDocumentChange(redo.finish(), reads.get());
      }

      if (method == ComputeMethod.Rewind) {
        String walkUndoSQL = new StringBuilder("SELECT `undo` FROM `") //
            .append(dataBase.databaseName).append("`.`deltas` WHERE `parent`=").append(lookup.id) //
            .append(" AND `seq_begin` >= ").append(seq) //
            .append(" ORDER BY `seq_begin` DESC").toString();
        AutoMorphicAccumulator<String> undo = JsonAlgebra.mergeAccumulator();
        AtomicInteger reads = new AtomicInteger(0);
        DataBase.walk(connection, (rs) -> {
          reads.incrementAndGet();
          metrics.read_rewind.run();
          undo.next(rs.getString(1));
        }, walkUndoSQL);
        if (undo.empty()) {
          throw new ErrorCodeException(ErrorCodes.COMPUTE_EMPTY_REWIND);
        }
        return new LocalDocumentChange(undo.finish(), reads.get());
      }

      throw new ErrorCodeException(ErrorCodes.COMPUTE_UNKNOWN_METHOD);
    }, callback, ErrorCodes.COMPUTE_FAILURE);
  }

  @Override
  public void delete(Key key, Callback<Void> callback) {
    dataBase.transact((connection) -> {
      // read the index
      metrics.delete.run();
      LookupResult lookup = lookup(connection, key);
      // update the index
      String deleteIndexSQL = new StringBuilder() //
          .append("DELETE FROM `").append(dataBase.databaseName).append("`.`index` ") //
          .append("WHERE `id`=").append(lookup.id).toString();
      DataBase.execute(connection, deleteIndexSQL);
      // build a list of all the history pointers and put them into a garbage collection queue
      String deleteDeltasSQL = new StringBuilder() //
          .append("DELETE FROM `").append(dataBase.databaseName).append("`.`deltas` ") //
          .append("WHERE `parent`=").append(lookup.id).toString();
      DataBase.execute(connection, deleteDeltasSQL);
      return null;
    }, callback, ErrorCodes.DELETE_FAILURE);
  }

  @Override
  public void compact(Key key, int history, Callback<Integer> callback) {
    dataBase.transact((connection) -> {
      // look up the index to get the id
      LookupResult lookup = lookup(connection, key);

      String walkSql = new StringBuilder("SELECT `id`, `redo`, `undo`, `seq_end`, `seq_begin`, `request` FROM `") //
          .append(dataBase.databaseName).append("`.`deltas` WHERE `parent`=").append(lookup.id) //
          .append(" ORDER BY `seq_end` DESC LIMIT ").append((history + 1) * 3 + 1000).append(" OFFSET ").append(history).toString();

      AutoMorphicAccumulator<String> redoMorph = JsonAlgebra.mergeAccumulator();
      AutoMorphicAccumulator<String> undoMorph = JsonAlgebra.mergeAccumulator();
      Stack<String> redoStack = new Stack<>();
      AtomicInteger count = new AtomicInteger(0);
      AtomicInteger end = new AtomicInteger(0);
      AtomicInteger begin = new AtomicInteger(Integer.MAX_VALUE);
      ArrayList<Integer> ids = new ArrayList<>();
      AtomicLong changeDeltaBytes = new AtomicLong(0);
      DataBase.walk(connection, (rs) -> {
        changeDeltaBytes.addAndGet(-(rs.getString(2).length() + rs.getString(3).length() + rs.getString(6).length()));
        ids.add(rs.getInt(1));
        redoStack.push(rs.getString(2));
        undoMorph.next(rs.getString(3));
        end.set(Math.max(end.get(), rs.getInt(4)));
        begin.set(Math.min(begin.get(), rs.getInt(5)));
        count.incrementAndGet();
        metrics.read_compact.run();
      }, walkSql);
      while (!redoStack.empty()) {
        redoMorph.next(redoStack.pop());
      }
      for (Integer id : ids) {
        String deleteDeltaById = new StringBuilder().append("DELETE FROM `").append(dataBase.databaseName).append("`.`deltas` ").append("WHERE `id`=").append(id).toString();
        DataBase.execute(connection, deleteDeltaById);
      }
      if (count.get() > 0) {
        String redoToUse = redoMorph.finish();
        String undoToUse = undoMorph.finish();
        changeDeltaBytes.addAndGet("{\"method\":\"compact\"}".length() + redoToUse.length() + undoToUse.length());
        metrics.write_compact.run();
        String insertCompactDeltaSQL = new StringBuilder() //
            .append("INSERT INTO `").append(dataBase.databaseName).append("`.`deltas` (") //
            .append("`parent`, `seq_begin`, `seq_end`, `who_agent`, `who_authority`, `request`, `redo`, `undo`, `history_ptr`) VALUES (") //
            .append(lookup.id).append(", ").append(begin.get()).append(", ").append(end.get()).append(", '?', 'adama', '{\"method\":\"compact\"}', ?, ?, '')") //
            .toString();
        PreparedStatement statement = connection.prepareStatement(insertCompactDeltaSQL);
        try {
          statement.setString(1, redoToUse);
          statement.setString(2, undoToUse);
          statement.execute();
        } finally {
          statement.close();
        }
        String updateIndexSQL = new StringBuilder() //
            .append("UPDATE `").append(dataBase.databaseName).append("`.`index` ") //
            .append("SET `delta_bytes`=`delta_bytes`+").append(changeDeltaBytes.get()) //
            .append(" WHERE `id`=").append(lookup.id).toString();
        DataBase.execute(connection, updateIndexSQL);
        metrics.lookup_change.run();

        // account for the one we insert
        count.decrementAndGet();
      }
      return count.get();
    }, callback, ErrorCodes.COMPUTE_FAILURE);
  }

  public static class LookupResult {
    public final int id;
    public final int head_seq;

    public LookupResult(int id, int head_seq) {
      this.id = id;
      this.head_seq = head_seq;
    }
  }
}
