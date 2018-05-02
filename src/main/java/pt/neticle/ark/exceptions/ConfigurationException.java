package pt.neticle.ark.exceptions;

public class ConfigurationException extends ArkRuntimeException
{
    public ConfigurationException ()
    {
    }

    public ConfigurationException (String message)
    {
        super(message);
    }

    public ConfigurationException (String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConfigurationException (Throwable cause)
    {
        super(cause);
    }
}
