package pt.neticle.ark.failsafe.handlers;

import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.output.Output;
import pt.neticle.ark.data.output.PlainText;
import pt.neticle.ark.exceptions.ArkRuntimeException;
import pt.neticle.ark.failsafe.ErrorHandler;
import pt.neticle.ark.failsafe.InternalErrorHandler;

public class FallbackErrorHandler implements ErrorHandler<DispatchContext>, InternalErrorHandler<DispatchContext>
{
    @Override
    public Output<?> handleError (DispatchContext context, ActionHandler matchedHandler, ArkRuntimeException exception)
    {
        return PlainText.buffered("[fallback] An error ocurred.");
    }

    @Override
    public Output<?> handleInternalError (DispatchContext context, ActionHandler matchedHandler, ArkRuntimeException exception)
    {
        return PlainText.buffered("[fallback] An internal error ocurred.");
    }
}
