package org.adamalang.mysql.backend;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.contracts.*;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.json.JsonAlgebra;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

/** Implements the DataService while blocking the caller's thread */
public class BlockingDataService implements DataService {
    private final DataBase dataBase;
    private final SimpleDateFormat dateFormat;

    public BlockingDataService(final DataBase dataBase) {
        this.dataBase = dataBase;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static class LookupResult {
        public final int id;
        public final int head_seq;

        public LookupResult(int id, int head_seq) {
            this.id = id;
            this.head_seq = head_seq;
        }
    }

    private String whenOf(RemoteDocumentUpdate patch) {
        return dateFormat.format(new Date(System.currentTimeMillis() + (patch.requiresFutureInvalidation ? patch.whenToInvalidateMilliseconds : 0)));
    }

    public LookupResult lookup(Connection connection, Key key) throws SQLException, ErrorCodeException {
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
                    throw new ErrorCodeException(ErrorCodes.LOOKUP_FAILED);
                }
            } finally {
                rs.close();
            }
        } finally {
            statement.close();
        }
    }

    /** internal: insert a delta */
    private void insertDelta(Connection connection, int parent, RemoteDocumentUpdate patch) throws SQLException {
        String insertDeltaSQL = new StringBuilder() //
                .append("INSERT INTO `").append(dataBase.databaseName).append("`.`deltas` (") //
                .append("`parent`, `seq_begin`, `seq_end`, `who_agent`, `who_authority`, `request`, `redo`, `undo`, `history_ptr`) VALUES (") //
                .append(parent).append(", '").append(patch.seq).append("', '").append(patch.seq).append("', ") //
                .append("?, ?, ?, ?, ?, '')") //
                .toString();

        PreparedStatement statement = connection.prepareStatement(insertDeltaSQL);
        try {
            statement.setString(1, patch.who.agent);
            statement.setString(2, patch.who.authority);
            statement.setString(3, patch.request);
            statement.setString(4, patch.redo);
            statement.setString(5, patch.undo);
            statement.execute();
        } finally {
            statement.close();
        }
    }

    @Override
    public void scan(ActiveKeyStream stream) {
        try {
            String scanSQL = new StringBuilder("SELECT `space`, `key`, `when` FROM `").append(dataBase.databaseName).append("`.`index` WHERE `invalidate` = 1").toString();
            try (Connection connection = dataBase.pool.getConnection() ) {
                DataBase.walk(connection, (rs) -> {
                    Key key = new Key(rs.getString(1), rs.getString(2));
                    long absolute = rs.getDate(3).getTime();
                    long now = System.currentTimeMillis();
                    long relative = absolute < now ? 0 : absolute - now;
                    stream.schedule(key, relative);
                }, scanSQL);
                stream.finish();
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // TODO: PROPER LOGGING
            stream.error(ErrorCodeException.detectOrWrap(ErrorCodes.SCAN_FAILURE, ex));
        }
    }

    @Override
    public void get(Key key, Callback<LocalDocumentChange> callback) {
        dataBase.transact((connection) -> {
            // look up the index to get the id
            LookupResult lookup = lookup(connection, key);
            String walkRedoSQL = new StringBuilder("SELECT `redo` FROM `").append(dataBase.databaseName).append("`.`deltas` WHERE `parent`=").append(lookup.id).append(" ORDER BY `seq_begin`").toString();
            AutoMorphicAccumulator<String> merge = JsonAlgebra.mergeAccumulator();
            DataBase.walk(connection, (rs) -> {
                merge.next(rs.getString(1));
            }, walkRedoSQL);
            return new LocalDocumentChange(merge.finish());
        }, callback, ErrorCodes.GET_FAILURE);
    }

    @Override
    public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
        dataBase.transact((connection) -> {
            // build the sql into insert
            String insertIndexSQL = new StringBuilder() //
                .append("INSERT INTO `").append(dataBase.databaseName).append("`.`index` (") //
                .append("`space`, `key`, `head_seq`, `invalidate`, `when`) VALUES (?, ?, ") //
                .append("'").append(patch.seq).append("', ") //
                .append("'").append(patch.requiresFutureInvalidation ? "1" : "0").append("', ") //
                .append("'").append(whenOf(patch)).append("')") //
                .toString();

            // execute the insert
            PreparedStatement statementInsertIndex = connection.prepareStatement(insertIndexSQL, Statement.RETURN_GENERATED_KEYS);
            try {
                statementInsertIndex.setString(1, key.space);
                statementInsertIndex.setString(2, key.key);
                statementInsertIndex.execute();

                // insert the delta
                int parent = DataBase.getInsertId(statementInsertIndex);
                insertDelta(connection, parent, patch);
            } finally {
                statementInsertIndex.close();
            }

            return null;
        }, callback, ErrorCodes.INITIALIZE_FAILURE);
    }

    @Override
    public void patch(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
        dataBase.transact((connection) -> {
            // read the index
            LookupResult lookup = lookup(connection, key);
            if (lookup.head_seq + 1 != patch.seq) {
                throw new ErrorCodeException(ErrorCodes.UNIVERSAL_PATCH_FAILURE_HEAD_SEQ_OFF);
            }

            // update the index
            String updateIndexSQL = new StringBuilder() //
              .append("UPDATE `").append(dataBase.databaseName).append("`.`index` ") //
              .append("SET `head_seq`=").append(patch.seq) //
              .append(", `invalidate`=").append(patch.requiresFutureInvalidation ? 1 : 0) //
              .append(", `when`='").append(whenOf(patch)) //
              .append("' WHERE `id`=").append(lookup.id).toString();
            DataBase.execute(connection, updateIndexSQL);

            // insert delta
            insertDelta(connection, lookup.id, patch);
            return null;
        }, callback, ErrorCodes.PATCH_FAILURE);
    }

    @Override
    public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
        dataBase.transact((connection) -> {
            // look up the index to get the id
            LookupResult lookup = lookup(connection, key);

            if (method == ComputeMethod.Patch) {
                String walkUndoSQL = new StringBuilder("SELECT `redo` FROM `") //
                        .append(dataBase.databaseName).append("`.`deltas` WHERE `parent`=").append(lookup.id) //
                        .append(" AND `seq_begin` > ").append(seq) //
                        .append(" ORDER BY `seq_begin` DESC").toString();
                AutoMorphicAccumulator<String> redo = JsonAlgebra.mergeAccumulator();
                DataBase.walk(connection, (rs) -> {
                    redo.next(rs.getString(1));
                }, walkUndoSQL);
                if (redo.empty()) {
                    throw new ErrorCodeException(ErrorCodes.COMPUTE_EMPTY_PATCH);
                }
                return new LocalDocumentChange(redo.finish());
            }

            if (method == ComputeMethod.Rewind) {
                String walkUndoSQL = new StringBuilder("SELECT `undo` FROM `") //
                        .append(dataBase.databaseName).append("`.`deltas` WHERE `parent`=").append(lookup.id) //
                        .append(" AND `seq_begin` >= ").append(seq) //
                        .append(" ORDER BY `seq_begin` DESC").toString();
                AutoMorphicAccumulator<String> undo = JsonAlgebra.mergeAccumulator();
                DataBase.walk(connection, (rs) -> {
                    undo.next(rs.getString(1));
                }, walkUndoSQL);
                if (undo.empty()) {
                    throw new ErrorCodeException(ErrorCodes.COMPUTE_EMPTY_REWIND);
                }
                return new LocalDocumentChange(undo.finish());
            }

            if (method == ComputeMethod.Unsend) {
                String getUndoSQL = new StringBuilder("SELECT `undo` FROM `") //
                        .append(dataBase.databaseName).append("`.`deltas` WHERE `parent`=").append(lookup.id) //
                        .append(" AND `seq_begin` = ").append(seq) //
                        .append(" AND `seq_end` = ").append(seq).toString();

                String walkRedoSQL = new StringBuilder("SELECT `redo` FROM `") //
                        .append(dataBase.databaseName).append("`.`deltas` WHERE `parent`=").append(lookup.id) //
                        .append(" AND `seq_begin` > ").append(seq) //
                        .append(" ORDER BY `seq_begin` ASC").toString();
                AtomicReference<AutoMorphicAccumulator<String>> unsend = new AtomicReference<>();
                int count = DataBase.walk(connection, (rs1) -> {
                    unsend.set(JsonAlgebra.rollUndoForwardAccumulator(rs1.getString(1)));
                    DataBase.walk(connection, (rs2) -> {
                        unsend.get().next(rs2.getString(1));
                    }, walkRedoSQL);
                }, getUndoSQL);
                if (count == 0) {
                    throw new ErrorCodeException(ErrorCodes.COMPUTE_EMPTY_UNSEND);
                }
                return new LocalDocumentChange(unsend.get().finish());
            }
            throw new ErrorCodeException(ErrorCodes.COMPUTE_UNKNOWN_METHOD);
        }, callback, ErrorCodes.COMPUTE_FAILURE);
    }

    @Override
    public void delete(Key key, Callback<Void> callback) {
        dataBase.transact((connection) -> {
            // read the index
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

}
