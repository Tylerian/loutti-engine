package net.luotti.engine.communication.events;

import com.luotti.engine.utilities.memory.IDisposable;
import net.luotti.engine.communication.sessions.Session;
import net.luotti.engine.communication.protocol.MessageRequest;

public interface IEventDispatcher extends IDisposable {
    public int getLoadFactor();
    public int getEventQueueSize();
    public int getEventQueueLimit();
    public int getExecutorPoolSize();
    public void resizeEventQueueSize(int size);
    public void resizeExecutorPoolSize(int size);
    public void enqueue(Session session, MessageRequest request);
}