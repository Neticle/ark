package pt.neticle.ark.exceptions;

public abstract class ArkRuntimeException extends RuntimeException
{
    public ArkRuntimeException ()
    {
        super();
    }

    public ArkRuntimeException (String message)
    {
        super(message);
    }

    public ArkRuntimeException (String message, Throwable cause)
    {
        super(message, cause);
    }

    public ArkRuntimeException (Throwable cause)
    {
        super(cause);
    }
}
