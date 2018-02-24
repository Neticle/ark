package pt.neticle.ark.data.output;

import pt.neticle.ark.data.*;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.http.HttpResponse;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class Html extends PlainText
{
    private Html (OutputStream os, Charset encoding)
    {
        super(os, encoding);
        contentType = MediaType.Text.HTML;
    }

    private Html (OutputStream os, InputStream is, Charset encoding, Runnable bufferFlipper)
    {
        super(os, is, encoding, bufferFlipper);
        contentType = MediaType.Text.HTML;
    }

    @Override
    public Html append (String text)
    {
        super.append(text);
        return this;
    }

    @Override
    public void setContentType (MediaType.Text contentType)
    {
        throw new ImplementationException("Html output object has an implicit media type text/html");
    }

    public static Html buffered (Charset encoding)
    {
        DynamicByteBuffer b = new DynamicByteBuffer(4096);

        return new Html
        (
            new DynamicByteBufferOutputStream(b),
            new DynamicByteBufferInputStream(b),
            encoding,
            b::flip
        );
    }

    public static Html buffered ()
    {
        return Html.buffered(ArkDataUtils.UTF_8_CHARSET);
    }

    public static Html buffered (String initialContent)
    {
        return Html.buffered().append(initialContent);
    }

    public static Html direct (HttpResponse response)
    {
        return new Html(response.contentOutput(), ArkDataUtils.UTF_8_CHARSET);
    }
}
