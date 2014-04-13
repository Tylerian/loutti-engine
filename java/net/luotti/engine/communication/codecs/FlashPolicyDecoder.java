package net.luotti.engine.communication.codecs;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.MessageToMessageDecoder;


@ChannelHandler.Sharable
public class FlashPolicyDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static final ByteBuf POLICY_RESPONSE = Unpooled.copiedBuffer(
        ("<?xml version=\"1.0\"?>\r\n" + "<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">\r\n" +
         "<cross-domain-policy>\r\n" + "<allow-access-from domain=\"*\" to-ports=\"*\" />\r\n" + "</cross-domain-policy>\0").
         toCharArray(), CharsetUtil.UTF_8
    );

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> objects) throws Exception
    {
        /***************************************************************************************
         * Check if the first byte is '<'. This is only possible for the flash policy request, *
         * as protocol messages are an encoded short. After this check fails, codec is removed *
         ***************************************************************************************/

        if (buffer.readByte() == 0x3C)
        {
            context.writeAndFlush( // send policy file
                    FlashPolicyDecoder.POLICY_RESPONSE
            ).addListener(ChannelFutureListener.CLOSE);

            // Discard policy request bytes
            buffer.skipBytes( buffer.readableBytes() );
        }

        else
        {
            buffer.resetReaderIndex();
            context.pipeline().remove(this);

            objects.add(buffer.readBytes(buffer.readableBytes()));
        }
    }
}