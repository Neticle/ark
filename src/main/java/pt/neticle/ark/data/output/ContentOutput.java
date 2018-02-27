package pt.neticle.ark.data.output;

import pt.neticle.ark.data.ContentType;

import java.util.Optional;

public interface ContentOutput<T> extends Output<T>
{
    ContentType getContentType ();

    default boolean hasContentType ()
    {
        return getContentType() != null;
    }

    default Optional<ContentType> contentType ()
    {
        return Optional.ofNullable(getContentType());
    }
}
