package pt.neticle.ark.data.output;

import pt.neticle.ark.data.*;
import pt.neticle.ark.exceptions.ExternalConditionException;
import pt.neticle.ark.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class PlainText extends Output<PlainText> implements CharsetEncoded
{
    private final Charset charsetEncoding;
    private final OutputStreamWriter writter;

    /* For usage with a provided existing buffer */
    PlainText (OutputStream os, Charset encoding)
    {
        super(os);
        charsetEncoding = encoding;
        contentType = MediaType.Text.PLAIN;
        writter = new OutputStreamWriter(output(), encoding);
    }

    /* For usage with a new buffer */
    PlainText (OutputStream os, InputStream is, Charset encoding, Runnable bufferFlipper)
    {
        super(os, is, bufferFlipper);
        charsetEncoding = encoding;
        contentType = MediaType.Text.PLAIN;
        writter = new OutputStreamWriter(output(), encoding);

        try
        {
            //writter.write('\ufeff');
            output().write(0xef);
            output().write(0xbb);
            output().write(0xbf);
        } catch(IOException e)
        {
            throw new ExternalConditionException(e);
        }
    }

    public void setContentType (MediaType.Text contentType)
    {
        this.contentType = contentType;
    }

    @Override
    public Charset getCharsetEncoding ()
    {
        return charsetEncoding;
    }

    @Override
    public void ready ()
    {
        try
        {
            writter.flush();
        } catch(IOException e)
        {
            throw new ExternalConditionException(e);
        }

        super.ready();
    }

    public PlainText append (String text)
    {
        try
        {
            writter.append(text);
        } catch(IOException e)
        {
            throw new ExternalConditionException(e);
        }

        return this;
    }

    public static PlainText buffered (Charset encoding)
    {
        DynamicByteBuffer b = new DynamicByteBuffer(4096);

        return new PlainText
        (
            new DynamicByteBufferOutputStream(b),
            new DynamicByteBufferInputStream(b),
            encoding,
            b::flip
        );
    }

    public static PlainText buffered ()
    {
        return buffered(ArkDataUtils.UTF_8_CHARSET);
    }

    public static PlainText buffered (String initialContent)
    {
        return buffered().append(initialContent);
    }

    public static PlainText direct (HttpResponse response)
    {
        return new PlainText(response.contentOutput(), ArkDataUtils.UTF_8_CHARSET);
    }
}