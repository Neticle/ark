package pt.neticle.ark.failsafe;

import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.output.Output;
import pt.neticle.ark.exceptions.ArkRuntimeException;
import pt.neticle.ark.exceptions.InputException;

public interface ErrorHandler<TCtx extends DispatchContext>
{
    default Output<?> handleError (TCtx context, ActionHandler matchedHandler, ArkRuntimeException exception)
    {
        return null;
    }

    static boolean accepts (Throwable e)
    {
        return e instanceof InputException;
    }
}
