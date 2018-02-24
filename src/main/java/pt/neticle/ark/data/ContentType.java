package pt.neticle.ark.data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContentType
{
    private final MediaType mediaType;
    private final Charset charset;
    private final Map<String, String> attributes;

    public ContentType (MediaType mediaType, Charset charset)
    {
        this.mediaType = mediaType;
        this.charset = charset;
        this.attributes = new HashMap<>();

        if(charset != null)
        {
            this.attributes.put("charset", charset.name().toLowerCase());
        }
    }

    private ContentType (String contentTypeStr) throws ParseException
    {
        String[] parts = contentTypeStr.split(";");

        if(parts.length < 1)
        {
            throw new ParseException("Invalid content-type format", contentTypeStr.length());
        }

        mediaType = MediaType.valueOfOr(parts[0].trim(), () -> new MediaType.Incomplete(parts[0].trim()));

        attributes = Arrays.stream(parts, 1, parts.length - 1)
            .map((p) -> p.trim().split("="))
            .filter((p) -> p.length == 2)
            .collect(Collectors.toMap((p) -> p[0].trim().toLowerCase(), (p) -> p[1].trim().toLowerCase()));

        if(attributes.containsKey("charset"))
        {
            charset = Charset.forName(attributes.get("charset"));
        }
        else
        {
            charset = null;
        }
    }

    public final MediaType getMediaType ()
    {
        return mediaType;
    }

    public final Optional<Charset> charset ()
    {
        return Optional.ofNullable(charset);
    }

    public final Charset getCharset ()
    {
        return charset;
    }

    public String getAttribute (String key)
    {
        return attributes.get(key);
    }

    public Optional<String> attribute(String key)
    {
        return Optional.ofNullable(attributes.get(key));
    }

    public static ContentType parse (String contentTypeString) throws ParseException
    {
        return new ContentType(contentTypeString);
    }

    public static Optional<ContentType> attemptParse (String contentTypeString)
    {
        try
        {
            return Optional.of(parse(contentTypeString));
        } catch(ParseException e)
        {
            return Optional.empty();
        }
    }

    @Override
    public String toString ()
    {
        String s = mediaType.toString();

        if(!attributes.isEmpty())
        {
            s += "; " + attributes.entrySet().stream()
                .map((e) -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("; "));
        }

        return s;
    }
}
