package pt.neticle.ark.exceptions;

/**
 * Used when there was an error caused by an unexpected and external occurrence.
 *
 * External condition exceptions are applicable when:
 *  - The error wasn't caused by mismatched, unexpected or incomplete input given by a client
 *  - The error wasn't caused by faulty internal implementation, missing or misconfigured policies, badly constructed
 *  injection points, etc.
 *
 *  Some examples of external conditions may be:
 *  - Failure to connect to a database (not configuration related)
 *  - Failure to write to disk (access denied, lack of space, etc)
 *  - Failure to reach a remote server
 */
public class ExternalConditionException extends ArkRuntimeException
{
    public ExternalConditionException ()
    {
        super();
    }

    public ExternalConditionException (String message)
    {
        super(message);
    }

    public ExternalConditionException (String message, Throwable cause)
    {
        super(message, cause);
    }

    public ExternalConditionException (Throwable cause)
    {
        super(cause);
    }
}
