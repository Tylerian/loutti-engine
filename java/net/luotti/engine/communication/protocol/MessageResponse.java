package net.luotti.engine.communication.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class MessageResponse extends IMessage {

    private static final short PAYLOAD_SIZE_THRESHOLD = 0x200;

    @Override
    public void destruct()
    {
        this.iOPCode = 0;
        this.mBuffer = null;
    }

    public void writeInt(int i)
    {
        this.mBuffer.writeInt(i);
    }

    public void writeUTF(String s)
    {
        if (s == null)
        {
            this.mBuffer.writeShort(0); return;
        }

        this.mBuffer.writeShort(s.length());
        this.mBuffer.writeBytes(s.getBytes());
    }

    public void writeBoolean(boolean b)
    {
        this.mBuffer.writeByte(b ? 1 : 0);
    }

    public ByteBuf getPayload()
    {
        this.mBuffer.setInt
        (0, this.mBuffer.writerIndex() - 4);
        return this.mBuffer;
    }

    public MessageResponse(short OPCode)
    {
        super(OPCode, PooledByteBufAllocator.DEFAULT.buffer(
            MessageResponse.PAYLOAD_SIZE_THRESHOLD
        ));

        this.mBuffer.writeInt(0); this.mBuffer.writeShort(OPCode);
    }
}