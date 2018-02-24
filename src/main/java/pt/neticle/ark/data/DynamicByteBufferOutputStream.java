package pt.neticle.ark.data;

import java.io.IOException;
import java.io.OutputStream;

public class DynamicByteBufferOutputStream extends OutputStream
{
    private final DynamicByteBuffer buffer;

    public DynamicByteBufferOutputStream (DynamicByteBuffer buffer)
    {
        this.buffer = buffer;
    }

    @Override
    public void write (int b) throws IOException
    {
        buffer.put((byte)b);
    }
}
