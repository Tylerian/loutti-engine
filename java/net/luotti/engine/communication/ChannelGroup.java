package net.luotti.engine.communication;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ChannelGroup {

    private String sIdentifier;
    private List<Channel> lChannels;
    private ChannelFutureListener mUnbinder;

    private static AtomicInteger UUID_GENERATOR;

    static {
        ChannelGroup.UUID_GENERATOR = new AtomicInteger();
    }

    // region #Accessors

    public String getIdentifier()
    {
        return this.sIdentifier;
    }

    // endregion

    // region #Methods

    public void clear()
    {
        synchronized (this.lChannels)
        {
            for (Channel channel : this.lChannels)
            {
                channel.closeFuture().removeListener(this.mUnbinder);
            }

            this.lChannels.clear();
        }
    }

    public void flush()
    {
        synchronized (this.lChannels)
        {
            for (Channel channel : this.lChannels)
            {
                channel.flush();
            }
        }
    }

    public void disconnect()
    {
        synchronized (this.lChannels)
        {
            for (Channel channel : this.lChannels)
            {
                channel.disconnect();
            }
        }
    }

    public void write(Object message)
    {
        synchronized (this.lChannels)
        {
            for (Channel channel : this.lChannels)
            {
                channel.write(message, channel.voidPromise());
            }
        }
    }

    public void bind(Channel channel)
    {
        synchronized (this.lChannels)
        {
            this.lChannels.add(channel);
        }

        channel.closeFuture().addListener(this.mUnbinder);
    }

    public void unbind(Channel channel)
    {
        synchronized (this.lChannels)
        {
            this.lChannels.remove(channel);
        }

        channel.closeFuture().removeListener(this.mUnbinder);
    }

    public void writeAndFlush(Object message)
    {
        synchronized (this.lChannels)
        {
            for (Channel channel : this.lChannels)
            {
                channel.writeAndFlush(message, channel.voidPromise());
            }
        }
    }

    // endregion

    // region #Constructors
    public ChannelGroup()
    {
        this.lChannels = new ArrayList<>();
        this.mUnbinder = new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {

            /* this.*/ unbind(future.channel());
            }
        };
    }

    public ChannelGroup construct(String identifier)
    {
        this.sIdentifier = ("CHANNEL-GROUP-" + identifier
            + ChannelGroup.UUID_GENERATOR.getAndIncrement()
        );

        return this;
    }
    // endregion

}
