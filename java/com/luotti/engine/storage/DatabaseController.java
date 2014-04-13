package com.luotti.engine.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import com.luotti.engine.Environment;
import com.luotti.engine.logging.LogLevel;
import com.luotti.engine.settings.Properties;
import com.luotti.engine.utilities.memory.IDisposable;

public class DatabaseController implements IDisposable {

    private HikariConfig mConfiguration;
    private HikariDataSource mDataSource;

    private static final long IDLE_TIMEOUT = TimeUnit.MINUTES.toMillis(10);
    private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(1);
    private static final long CONNECTION_LIFETIME = TimeUnit.MINUTES.toMillis(30);
    private static final long CONNECTION_LEAK_THRESHOLD = TimeUnit.SECONDS.toMillis(10);

    private static final String KEY_SERVER_PORT = "port".intern();
    private static final String KEY_SERVER_NAME = "serverName".intern();
    private static final String KEY_DATABASE_NAME = "databaseName".intern();
    private static final String KEY_CREDENTIALS_NAME = "user".intern();
    private static final String KEY_CREDENTIALS_PASSWORD = "password".intern();

    private static final String CONNECTION_TEST_QUERY = "SELECT 1".intern();
    private static final String CONNECTION_DATA_DRIVER = "com.mysql.jdbc.Driver".intern();
    private static final String CONNECTION_DATA_SOURCE = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource".intern();

    // region #Constructors
    public DatabaseController()
    {
        this.mConfiguration = new HikariConfig();

        this.mConfiguration.setMaximumPoolSize(Properties.DATABASE_POOL_SIZE);

        this.mConfiguration.setIdleTimeout(DatabaseController.IDLE_TIMEOUT);
        this.mConfiguration.setMaxLifetime(DatabaseController.CONNECTION_LIFETIME);
        this.mConfiguration.setConnectionTimeout(DatabaseController.CONNECTION_TIMEOUT);
        this.mConfiguration.setDataSourceClassName(DatabaseController.CONNECTION_DATA_SOURCE);
        this.mConfiguration.setLeakDetectionThreshold(DatabaseController.CONNECTION_LEAK_THRESHOLD);

        /*
         * this.mConfiguration.setDriverClassName(DatabaseController.CONNECTION_DATA_DRIVER);
         * this.mConfiguration.setConnectionTestQuery(DatabaseController.CONNECTION_TEST_QUERY);
         */

        this.mConfiguration.addDataSourceProperty(KEY_DATABASE_NAME, Properties.DATABASE_NAME);
        this.mConfiguration.addDataSourceProperty(KEY_SERVER_NAME, Properties.DATABASE_SERVER_NAME);
        this.mConfiguration.addDataSourceProperty(KEY_SERVER_PORT, Properties.DATABASE_SERVER_PORT);
        this.mConfiguration.addDataSourceProperty(KEY_CREDENTIALS_NAME, Properties.DATABASE_CREDENTIALS_NAME);
        this.mConfiguration.addDataSourceProperty(KEY_CREDENTIALS_PASSWORD, Properties.DATABASE_CREDENTIALS_PASSWORD);
    }
    // endregion

    // region #Methods
    @Override
    public void destruct()
    {
        this.mDataSource.shutdown();

        this.mDataSource = null;
        this.mConfiguration = null;
    }

    public boolean bootstrap()
    {
        this.mDataSource = new HikariDataSource(this.mConfiguration);

        return this.testDatabaseConnection();
    }

    private boolean testDatabaseConnection()
    {
        boolean result = false;
        Connection test = null;

        try
        {
            test = this.getConnection();
            result = (test != null); test.close();
        }

        catch (Exception ex)
        {
            result = false;
        }

        if (result == false)
        {
            Environment.printOutBootError("Error in database connection pool initialization. Wrong database details.");
        }

        return result;
    }

    public void releaseConnection(Connection connection)
    {
        try
        {
            connection.close();
        }

        catch (Exception ex)
        {
            Environment.getLogger().printOut(LogLevel.CRITICAL, "SQLConnection.dispose() has thrown an exception.", ex);
        }
    }
    // endregion

    // region #Accessors

    public int getActiveConnections()
    {
        // Hikari is keeping alive pool size!
        return Properties.DATABASE_POOL_SIZE;
    }

    public Connection getConnection() throws SQLException
    {
        return this.mDataSource.getConnection();
    }
    // endregion
}
