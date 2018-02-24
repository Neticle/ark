package pt.neticle.ark.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * An output stream implementation for writing into a byte buffer.
 */
public class ByteBufferOutputStream extends OutputStream
{
    private final ByteBuffer buffer;

    public ByteBufferOutputStream (ByteBuffer buffer)
    {
        this.buffer = buffer;
    }

    @Override
    public void write (int b) throws IOException
    {
        buffer.put((byte)b);
    }
}
