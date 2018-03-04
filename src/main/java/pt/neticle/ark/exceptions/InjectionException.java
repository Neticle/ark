package pt.neticle.ark.exceptions;

public class InjectionException extends ArkRuntimeException
{
    protected InjectionException ()
    {
    }

    protected InjectionException (String message)
    {
        super(message);
    }

    protected InjectionException (String message, Throwable cause)
    {
        super(message, cause);
    }

    protected InjectionException (Throwable cause)
    {
        super(cause);
    }

    public static class NoSuitableInjector extends InjectionException
    {
        private final Class<?> requestedType;

        public NoSuitableInjector (Class<?> requestedType)
        {
            super("No injector available for " + requestedType.getName() + " type");
            this.requestedType = requestedType;
        }

        public Class<?> getRequestedType ()
        {
            return requestedType;
        }
    }
}
