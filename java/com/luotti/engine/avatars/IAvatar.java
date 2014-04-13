package com.luotti.engine.avatars;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.luotti.engine.utilities.memory.IDisposable;
import net.luotti.engine.communication.sessions.Session;


public interface IAvatar extends IDisposable {
    public long getID();
    public String getName();
    public Session getSession();
    public void fireOnlineTriggers();
    public void fireOfflineTriggers();
    public void setSession(Session session);
    public IAvatar construct(ResultSet rs) throws SQLException;
}
