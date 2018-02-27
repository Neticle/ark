package pt.neticle.ark.data.output;

import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.exceptions.ExternalConditionException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class Text<T> extends BufferedOutput<T> implements CharsetEncoded
{
    private final OutputStreamWriter writter;
    private ContentType contentType;

    /* For usage with a provided existing buffer */
    protected Text (OutputStream os, ContentType contentType)
    {
        super(os);

        this.contentType = contentType;

        writter = new OutputStreamWriter(output(), this.contentType.getCharset());
    }

    /* For usage with a internal buffer */
    protected Text (ContentType contentType)
    {
        super();

        this.contentType = contentType;

        writter = new OutputStreamWriter(output(), this.contentType.getCharset());

        if(this.contentType.getCharset().name().equals(StandardCharsets.UTF_8.name()))
        {
            try
            {
                output().write(0xef);
                output().write(0xbb);
                output().write(0xbf);
            } catch(IOException e)
            {
                throw new ExternalConditionException(e);
            }
        }
    }

    protected void appendString (String text)
    {
        try
        {
            writter.append(text);
        } catch(IOException e)
        {
            throw new ExternalConditionException(e);
        }
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

    @Override
    public Charset getCharsetEncoding ()
    {
        return contentType.getCharset();
    }

    @Override
    public ContentType getContentType ()
    {
        return this.contentType;
    }

}
