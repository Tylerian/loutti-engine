package net.luotti.engine.communication.events;

import net.luotti.engine.communication.protocol.MessageRequest;

public interface IMessageEvent {
    public void parse(MessageRequest request);
}
