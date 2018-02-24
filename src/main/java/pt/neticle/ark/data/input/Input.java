package pt.neticle.ark.data.input;

import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.exceptions.InputException;

/**
 * An input object matches dispatch context parameters. If a parameter is matched, it's text value will be converted
 * to the appropriate and expected type.
 *
 * If no parameter is matched, a {@link pt.neticle.ark.exceptions.InputException.MissingParameters} will be raised.
 *
 * @param <T> The expected value type for the input
 */
public class Input<T>
{
    private final String name;
    private final Class<T> dataType;
    private final T data;
    private final String originalTextValue;

    public Input (DispatchContext context, String name, Class<T> dataType)
    {
        originalTextValue = context
            .parameters()
            .filter((p) -> p.getKey().equals(name))
            .map((p) -> p.getValue())
            .findFirst().orElseThrow(() -> new InputException.MissingParameters(name));

        if(dataType.isAssignableFrom(String.class))
        {
            data = (T)originalTextValue;
        }
        else
        {
            data = context.getEnvironment().getIOConverter().transform(originalTextValue, String.class).into(dataType)
                .orElseThrow(() -> new InputException.MalformedData("Invalid format for supplied value of " + name + " parameter"));
        }

        this.dataType = dataType;
        this.name = name;
    }

    public T get ()
    {
        return data;
    }

    public String getOriginalTextValue ()
    {
        return originalTextValue;
    }

    public Class<T> getDataType ()
    {
        return dataType;
    }

    public String getName ()
    {
        return name;
    }
}
