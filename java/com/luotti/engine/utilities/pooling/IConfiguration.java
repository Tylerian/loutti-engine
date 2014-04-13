package com.luotti.engine.utilities.pooling;

public interface IConfiguration {

    public String getUsername();
    public String getPassword();
    public String getDatabaseURL();

    public boolean isReadonly();
    public boolean canFailfast();
    public boolean canAutocommit();

    public long getIdleTimeout();
    public long getConnectionTimeout();
    public long getConnectionLifetime();
    public long getConnectionLeakThreshold();

}
