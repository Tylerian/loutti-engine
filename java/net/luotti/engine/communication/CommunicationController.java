package net.luotti.engine.communication;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import com.luotti.engine.utilities.memory.IDisposable;

import net.luotti.engine.communication.events.EventDispatcher;
import net.luotti.engine.communication.events.IEventDispatcher;
import net.luotti.engine.communication.handlers.ChannelHandler;
import net.luotti.engine.communication.handlers.TrackingHandler;
import net.luotti.engine.communication.codecs.FlashPolicyDecoder;
import net.luotti.engine.communication.sessions.SessionController;
import net.luotti.engine.communication.codecs.MessageRequestDecoder;

public class CommunicationController extends ChannelInitializer<SocketChannel> implements IDisposable {

    private ChannelHandler mChannelHandler;
    private TrackingHandler mTrackingHandler;
    private IEventDispatcher mEventDispatcher;
    private FlashPolicyDecoder mPolicyDecoder;
    private SessionController mSessionController;
    private MessageRequestDecoder mRequestDecoder;

    public static ServerBootstrap BOOTSTRAP;
    public static final int CHANNEL_MEMORY_LIMIT = (4096 * 2);
    public static final long GLOBAL_MEMORY_LIMIT = (Runtime.getRuntime().maxMemory() / 3l);

    // region #Accesors
    public SessionController getSessions()
    {
        return this.mSessionController;
    }

    public IEventDispatcher getEventDispatcher()
    {
        return this.mEventDispatcher;
    }
    // endregion

    // region #Methods
    @Override
    public void destruct()
    {
        this.mTrackingHandler.release();
        this.mEventDispatcher.destruct();
        this.mSessionController.destruct();

        this.mEventDispatcher = null;
        this.mTrackingHandler = null;
        this.mSessionController = null;

        CommunicationController.BOOTSTRAP.group().shutdownGracefully();
        CommunicationController.BOOTSTRAP.childGroup().shutdownGracefully();
    }

    public boolean bootstrap()
    {
        this.mChannelHandler = new ChannelHandler();
        this.mPolicyDecoder  = new FlashPolicyDecoder();
        this.mRequestDecoder = new MessageRequestDecoder();
        this.mTrackingHandler = new TrackingHandler(GlobalEventExecutor.INSTANCE);
        return true;
    }

    public boolean initializeNetworking() {
        return CommunicationBootstrap.bootstrap();
    }

    public boolean initializeTrafficProfiler() {
        this.mTrackingHandler.trafficCounter().start(); return true;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception
    {
        channel.pipeline().addLast("tracking", this.mTrackingHandler);
        channel.pipeline().addLast("policy_decoder", this.mPolicyDecoder);
        channel.pipeline().addLast("request_decoder", this.mRequestDecoder);
        channel.pipeline().addLast("channel_handler", this.mChannelHandler);
    }
    // endregion

    // region #Constructors
    public CommunicationController()
    {
        this.mEventDispatcher = new EventDispatcher();
        this.mSessionController = new  SessionController();
    }
    // endregion
}
