package com.luotti.engine.utilities.pooling;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnectionCustomizer {

    public void customize(Connection connection) throws SQLException;
}
