package net.luotti.engine.communication.events;

import net.luotti.engine.communication.protocol.MessageRequest;

public interface IEventListener {
    public String getIdentifier();
    public void invoke(MessageRequest request);
}