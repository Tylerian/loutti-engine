package com.luotti.engine.utilities.pooling;

import java.util.concurrent.TimeUnit;

public class Configuration implements IConfiguration {

    private String sDataDriver;
    private String sDataSource;

    private String sUsername;
    private String sPassword;
    private String sDatabaseURL;

    private boolean bFailfast;
    private boolean bReadonly;
    private boolean bAutocommit;

    private volatile long iIdleTimeout;
    private volatile long iConnectionTimeout;
    private volatile long iConnectionLifetime;
    private volatile long iConnectionLeakThreshold;

    private IConnectionCustomizer mConnectionCustomizer;

    private static final long IDLE_TIMEOUT = TimeUnit.MINUTES.toMillis(10);
    private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(30);
    private static final long CONNECTION_LIFETIME = TimeUnit.MINUTES.toMillis(30);
    private static final long CONNECTION_LEAK_THRESHOLD = TimeUnit.SECONDS.toMillis(10);

    private static final String CONNECTION_TEST_QUERY = "SELECT 1".intern();
    private static final String CONNECTION_DATA_DRIVER = "com.mysql.jdbc.Driver".intern();
    private static final String CONNECTION_DATA_SOURCE = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource".intern();

    public Configuration()
    {
        this.sDataDriver = Configuration.CONNECTION_DATA_DRIVER;
        this.sDataSource = Configuration.CONNECTION_DATA_SOURCE;

        this.iIdleTimeout = Configuration.IDLE_TIMEOUT;
        this.iConnectionTimeout = Configuration.CONNECTION_TIMEOUT;
        this.iConnectionLifetime = Configuration.CONNECTION_LIFETIME;
    }

    @Override
    public String getUsername()
    {
        return this.sUsername;
    }

    @Override
    public String getPassword()
    {
        return this.sPassword;
    }

    public String getDataDriver()
    {
        return this.sDataDriver;
    }

    public String getsDataSource()
    {
        return this.sDataSource;
    }

    @Override
    public String getDatabaseURL()
    {
        return this.sDatabaseURL;
    }

    @Override
    public long getIdleTimeout()
    {
        return this.iIdleTimeout;
    }

    @Override
    public long getConnectionTimeout()
    {
        return this.iConnectionTimeout;
    }

    @Override
    public long getConnectionLifetime()
    {
        return this.iConnectionLifetime;
    }

    @Override
    public long getConnectionLeakThreshold()
    {
        return this.iConnectionLeakThreshold;
    }

    @Override
    public boolean isReadonly()
    {
        return this.bReadonly;
    }

    @Override
    public boolean canFailfast()
    {
        return this.bFailfast;
    }

    @Override
    public boolean canAutocommit()
    {
        return this.bAutocommit;
    }

    public void toggleFailfast()
    {
        this.bFailfast = !this.bFailfast;
    }

    public void toggleReadonly()
    {
        this.bReadonly = !this.bReadonly;
    }

    public void toggleAutocommit()
    {
        this.bAutocommit = !this.bAutocommit;
    }

    public void setUsername(String username)
    {
        this.sUsername = username;
    }

    public void setPassword(String password)
    {
        this.sPassword = password;
    }

    public void setDatabaseURL(String dbURL)
    {
        this.sDatabaseURL = dbURL;
    }

    public void setDataDriver(String namespace)
    {
        this.sDataDriver = namespace;
    }

    public void setDataSource(String namespace)
    {
        this.sDataSource = namespace;
    }

    public void enableConnectionLeakDetection()
    {
        this.iConnectionLeakThreshold = Configuration.CONNECTION_LEAK_THRESHOLD;
    }

    public IConnectionCustomizer getConnectionCustomizer()
    {
        return this.mConnectionCustomizer;
    }
}
