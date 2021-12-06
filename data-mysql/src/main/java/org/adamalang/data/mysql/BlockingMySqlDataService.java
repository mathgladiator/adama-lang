package org.adamalang.data.mysql;

import org.adamalang.runtime.contracts.ActiveKeyStream;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BlockingMySqlDataService implements DataService {
    private final MySqlBase base;
    private final SimpleDateFormat dateFormat;

    public BlockingMySqlDataService(final MySqlBase base) {
        this.base = base;
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
        PreparedStatement statement = connection.prepareStatement(new StringBuilder("SELECT `id`, `head_seq` FROM `").append(base.databaseName).append("`.`index` WHERE `space`=? AND `key`=? LIMIT 1").toString());
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
                    throw new ErrorCodeException(111);
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
                .append("INSERT INTO `").append(base.databaseName).append("`.`deltas` (") //
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
            String scanSQL = new StringBuilder("SELECT `space`, `key`, `when` FROM `").append(base.databaseName).append("`.`index` WHERE `invalidate` = 1").toString();
            Connection connection = base.pool.getConnection();
            try {
                MySqlBase.walk(connection, (rs) -> {
                    try {
                        Key key = new Key(rs.getString(1), rs.getString(2));
                        long absolute = rs.getDate(3).getTime();
                        long now = System.currentTimeMillis();
                        long relative = absolute < now ? 0 : absolute - now;
                        stream.schedule(key, relative);
                    } catch (SQLException ex) {
                        // TODO: WTF, maybe ignore
                        ex.printStackTrace();
                    }
                }, scanSQL);
                stream.finish();
            } finally {
                connection.close();
            }
        } catch (Exception ex) {
            // TODO: shut down the adama server?
            ex.printStackTrace();
        }
    }

    @Override
    public void get(Key key, Callback<LocalDocumentChange> callback) {
        base.transact((connection) -> {
            // look up the index to get the id
            LookupResult lookup = lookup(connection, key);
            String walkRedoSQL = new StringBuilder("SELECT `redo` FROM `").append(base.databaseName).append("`.`deltas` WHERE `parent`=").append(lookup.id).append(" ORDER BY `seq_begin`").toString();
            MySqlBase.walk(connection, (rs) -> {
                try {
                    String redo = rs.getString(1);
                    System.err.println(redo);
                    // TODO: parse the JSON
                    // TODO: merge the JSON
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }, walkRedoSQL);
            return null;
        }, callback);
    }

    @Override
    public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
        base.transact((connection) -> {
            // build the sql into insert
            String insertIndexSQL = new StringBuilder() //
                .append("INSERT INTO `").append(base.databaseName).append("`.`index` (") //
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
                int parent = MySqlBase.getInsertId(statementInsertIndex);
                insertDelta(connection, parent, patch);
            } finally {
                statementInsertIndex.close();
            }

            return null;
        }, callback);
    }

    @Override
    public void patch(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
        base.transact((connection) -> {
            // read the index
            LookupResult lookup = lookup(connection, key);
            if (lookup.head_seq + 1 != patch.seq) {
                throw new ErrorCodeException(500); // duplicate
            }

            // update the index
            String updateIndexSQL = new StringBuilder() //
              .append("UPDATE `").append(base.databaseName).append("`.`index` ") //
              .append("SET `head_seq`=").append(patch.seq) //
              .append(", `invalidate`=").append(patch.requiresFutureInvalidation ? 1 : 0) //
              .append(", `when`='").append(whenOf(patch)) //
              .append("' WHERE `id`=").append(lookup.id).toString();
            MySqlBase.execute(connection, updateIndexSQL);

            // insert delta
            insertDelta(connection, lookup.id, patch);
            return null;
        }, callback);
    }

    @Override
    public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
        base.transact((connection) -> {
            // look up the index to get the id
            LookupResult lookup = lookup(connection, key);

            if (method == ComputeMethod.Rewind) {
                String walkUndoSQL = new StringBuilder("SELECT `undo` FROM `") //
                        .append(base.databaseName).append("`.`deltas` WHERE `parent`=").append(lookup.id) //
                        .append(" AND `seq_begin` >= ").append(seq) //
                        .append(" ORDER BY `seq_begin` DESC").toString();
                MySqlBase.walk(connection, (rs) -> {
                    try {
                        String undo = rs.getString(1);
                        System.err.println(undo);
                        // TODO: use accumulator
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }, walkUndoSQL);

                // TODO: return finished
            }

            if (method == ComputeMethod.Unsend) {
                // TODO: get the UNDO at seq
                // TODO: walk the REDO past seq
            }


            return null;
        }, callback);
    }

    @Override
    public void delete(Key key, Callback<Void> callback) {
        base.transact((connection) -> {
            // read the index
            LookupResult lookup = lookup(connection, key);
            // update the index
            String deleteIndexSQL = new StringBuilder() //
                    .append("DELETE FROM `").append(base.databaseName).append("`.`index` ") //
                    .append("WHERE `id`=").append(lookup.id).toString();
            MySqlBase.execute(connection, deleteIndexSQL);

            // build a list of all the history pointers and put them into a garbage collection queue

            String deleteDeltasSQL = new StringBuilder() //
                    .append("DELETE FROM `").append(base.databaseName).append("`.`deltas` ") //
                    .append("WHERE `parent`=").append(lookup.id).toString();
            MySqlBase.execute(connection, deleteDeltasSQL);
            return null;
        }, callback);
    }

}
