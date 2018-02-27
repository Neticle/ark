package pt.neticle.ark.data.output;

import pt.neticle.ark.data.*;
import pt.neticle.ark.http.HttpResponse;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PlainText extends Text<PlainText>
{
    public PlainText (ContentType contentType)
    {
        super(contentType);
    }

    public PlainText (OutputStream os, ContentType contentType)
    {
        super(os, contentType);
    }

    public PlainText append (String text)
    {
        appendString(text);
        return this;
    }

    public static PlainText buffered (ContentType contentType)
    {
        return new PlainText
        (
            contentType
        );
    }

    public static PlainText buffered ()
    {
        return buffered(new ContentType(MediaType.Text.PLAIN, StandardCharsets.UTF_8));
    }

    public static PlainText buffered (String initialContent)
    {
        return buffered().append(initialContent);
    }

    public static PlainText direct (HttpResponse response)
    {
        return new PlainText(response.contentOutput(), new ContentType(MediaType.Text.PLAIN, StandardCharsets.UTF_8));
    }
}