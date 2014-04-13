package net.luotti.engine.communication.handlers;

import com.luotti.engine.Environment;
import com.luotti.engine.logging.LogLevel;
import com.luotti.engine.settings.Properties;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import io.netty.util.AttributeKey;
import net.luotti.engine.communication.sessions.Session;
import net.luotti.engine.communication.protocol.MessageRequest;
import net.luotti.engine.communication.sessions.SessionController;

@io.netty.channel.ChannelHandler.Sharable
public class ChannelHandler extends ChannelInboundHandlerAdapter {

    private static AttributeKey<Session> SESSION;

    static {
        ChannelHandler.SESSION = AttributeKey.valueOf("channel.session");
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception
    {
        super.channelActive(context);

        SessionController.CHANNELS.bind(context.channel());

        context.attr(ChannelHandler.SESSION).set(
            Environment.getCommunication().getSessions().addConnection(context.channel())
        );
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception
    {
        super.channelInactive(context);

        SessionController.CHANNELS.unbind(context.channel());
        Environment.getCommunication().getSessions().removeConnection(context.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception
    {
        super.channelRead(context, message);

        long start = Environment.traceNanoTime();

        if (message instanceof MessageRequest)
        {
            MessageRequest request = (MessageRequest) message;
            Session session = context.attr(SESSION).get(); session.read(request);
            Environment.getCommunication().getEventDispatcher().enqueue(session, request);
        }

        long pause = Environment.traceNanoTime();

        if (Properties.MESSAGE_REQUEST_PROFILING)
        {
            Environment.getLogger().printOut(LogLevel.TRACE, "Took " + (pause - start) /  1000000.0D + "ms to enqueue the request.");
        }
    }
}
