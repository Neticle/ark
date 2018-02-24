package pt.neticle.ark.exceptions;

public abstract class ArkCheckedException extends Exception
{
    public ArkCheckedException ()
    {
    }

    public ArkCheckedException (String message)
    {
        super(message);
    }

    public ArkCheckedException (String message, Throwable cause)
    {
        super(message, cause);
    }

    public ArkCheckedException (Throwable cause)
    {
        super(cause);
    }
}
