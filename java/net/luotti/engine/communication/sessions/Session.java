package net.luotti.engine.communication.sessions;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.channel.Channel;

import com.luotti.engine.Environment;
import com.luotti.engine.avatars.IAvatar;
import com.luotti.engine.logging.LogLevel;

import net.luotti.engine.communication.codecs.BinaryDecoder;
import net.luotti.engine.communication.protocol.MessageRequest;
import net.luotti.engine.communication.protocol.MessageResponse;


public class Session {

    private long iID;
    private IAvatar mAvatar;
    private Channel mChannel;
    private String sIPAddress;
    private long iLastReadTime;
    private static AtomicLong SESSION_ID;

    static {
        Session.SESSION_ID = new AtomicLong();
    }

    public long getID()
    {
        return this.iID;
    }

    public IAvatar getAvatar()
    {
        return this.mAvatar;
    }

    public Channel getChannel()
    {
        return this.mChannel;
    }

    public String getIPAddress()
    {
        return this.sIPAddress;
    }

    public long getLastReadTime()
    {
        return this.iLastReadTime;
    }

    public void fireOnlineTriggers()
    {
        this.mAvatar.fireOnlineTriggers();
    }

    public void fireOfflineTriggers()
    {
        this.mAvatar.fireOfflineTriggers();
    }

    public void setAvatar(IAvatar avatar)
    {
        this.mAvatar = avatar;
    }

    public Session construct(Channel channel)
    {
        this.mChannel = channel;
        this.iID = Session.SESSION_ID.incrementAndGet();
        this.sIPAddress = ((InetSocketAddress) channel.
        remoteAddress()).getAddress().getHostAddress();
        this.iLastReadTime = Environment.traceMilliTime();
        return this;
    }

    public void read(MessageRequest request)
    {
        this.iLastReadTime = Environment.traceMilliTime();
        Environment.getLogger().printOut(LogLevel.DEBUG, "[" + this.iID + "|RCV][#" + request.getOPCode() + "]: " + BinaryDecoder.parse(request.toString()));
    }

    public void write(MessageResponse response)
    {
        this.mChannel.writeAndFlush(response.getPayload(), this.mChannel.voidPromise());
        Environment.getLogger().printOut(LogLevel.DEBUG, "[" + this.iID + "|SND][#" + response.getOPCode() + "]: " + BinaryDecoder.parse(response.toString()));
    }
}
