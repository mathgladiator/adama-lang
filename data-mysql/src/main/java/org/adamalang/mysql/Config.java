package org.adamalang.mysql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.adamalang.runtime.json.JsonStreamReader;

import java.io.File;
import java.nio.file.Files;

/** defines the config for the mysql data service */
public class Config {
    public final String jdbcUrl;
    public final String user;
    public final String password;
    public final String databaseName;

    public Config(File file) throws Exception {
        this(Files.readString(file.toPath()));
    }

    public Config(String json) {
        JsonStreamReader reader = new JsonStreamReader(json);
        String _jdbcUrl = null;
        String _user = null;
        String _password = null;
        String _databaseName = null;
        if (reader.startObject()) {
            while (reader.notEndOfObject()) {
                switch (reader.fieldName()) {
                    case "jdbc_url":
                        _jdbcUrl = reader.readString();
                        break;
                    case "user":
                        _user = reader.readString();
                        break;
                    case "password":
                        _password = reader.readString();
                        break;
                    case "database_name":
                        _databaseName = reader.readString();
                        break;
                }
            }
        }
        if (_jdbcUrl == null) {
            throw new NullPointerException("jdbc_url was not present in config");
        }
        if (_user == null) {
            throw new NullPointerException("user was not present in config");
        }
        if (_password == null) {
            throw new NullPointerException("password was not present in config");
        }
        if (_databaseName == null) {
            throw new NullPointerException("database_name was not present in config");
        }
        this.jdbcUrl = _jdbcUrl;
        this.user = _user;
        this.password = _password;
        this.databaseName = _databaseName;
    }

    public ComboPooledDataSource createComboPooledDataSource() throws Exception {
        ComboPooledDataSource pool = new ComboPooledDataSource();
        pool.setDriverClass( "com.mysql.cj.jdbc.Driver" ); //loads the jdbc driver
        pool.setJdbcUrl(jdbcUrl);
        pool.setUser(user);
        pool.setPassword(password);

        // TODO: make this part of the config
        pool.setMaxStatements(0);
        pool.setMaxStatementsPerConnection(0);
        pool.setMaxPoolSize(32);
        pool.setMinPoolSize(16);
        pool.setInitialPoolSize(16);
        return pool;
    }
}
