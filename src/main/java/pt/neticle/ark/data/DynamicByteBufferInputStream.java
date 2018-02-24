package pt.neticle.ark.data;

import java.io.IOException;
import java.io.InputStream;

public class DynamicByteBufferInputStream extends InputStream
{
    private final DynamicByteBuffer buffer;

    public DynamicByteBufferInputStream (DynamicByteBuffer buffer)
    {
        this.buffer = buffer;
    }

    @Override
    public int read () throws IOException
    {
        if(!buffer.hasRemaining()) return -1;
        return buffer.get() & 0xFF;
    }
}
