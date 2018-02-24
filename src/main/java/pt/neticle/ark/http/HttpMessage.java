package pt.neticle.ark.http;

import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.data.MediaType;
import pt.neticle.ark.data.Pair;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Common methods for both HttpRequest and HttpResponse
 */
public interface HttpMessage
{
    String getHeader (String header);

    ContentType getContentType ();

    default Optional<ContentType> contentType ()
    {
        return Optional.ofNullable(getContentType());
    }
}
