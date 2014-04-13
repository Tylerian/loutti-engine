package net.luotti.engine.communication.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.util.concurrent.EventExecutor;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

@ChannelHandler.Sharable
public class TrackingHandler extends GlobalTrafficShapingHandler {

    // region #Accessors
    public long getIncomingTraffic()
    {
        return this.trafficCounter.currentReadBytes();
    }

    public long getOutgoingTraffic()
    {
        return this.trafficCounter.currentWrittenBytes();
    }

    public long getTotalIncomingTraffic()
    {
        return this.trafficCounter.cumulativeReadBytes();
    }

    public long getTotalOutgoingTraffic()
    {
        return this.trafficCounter.cumulativeWrittenBytes();
    }
    // endregion

    // region #Constructors
    public TrackingHandler(EventExecutor executor)
    {
        super(executor, DEFAULT_CHECK_INTERVAL);
    }
    // endregion
}