package pt.neticle.ark.data.structured.builder;

/**
 * An utility class to aid the creation of structured data.
 */
public class StructuredDataBuilder
{
    public static AssociativeObject object ()
    {
        return new AssociativeObject();
    }

    public static ListObject list ()
    {
        return new ListObject();
    }

    public static <T> LiteralObject<T> literal (T value)
    {
        return new LiteralObject<>(value);
    }
}
