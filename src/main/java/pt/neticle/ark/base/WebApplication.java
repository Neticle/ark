package pt.neticle.ark.base;

import pt.neticle.ark.exceptions.InputException;
import pt.neticle.ark.http.HttpDispatchContext;
import pt.neticle.ark.http.HttpRequest;
import pt.neticle.ark.http.HttpResponse;
import pt.neticle.ark.injection.InjectionPolicy;
import pt.neticle.ark.injection.InlineInjectionPolicy;
import pt.neticle.ark.runtime.Cast;

public class WebApplication extends Application
{
    public WebApplication ()
    {
        super();
        configure();
    }

    public WebApplication (PolicyHoldingContext mainContext)
    {
        super(mainContext);
        configure();
    }

    private void configure ()
    {
        context().addPolicy(new InlineInjectionPolicy<>(
            HttpRequest.class,
            InjectionPolicy.ObjectLifespan.DISPOSABLE,
            (requestingContext, name, typeData) ->
            {
                HttpDispatchContext context = Cast.attempt(HttpDispatchContext.class, requestingContext)
                    .orElseThrow(() -> new InputException.PreconditionFailed("This action requires access through HTTP"));

                return context.getRequest();
            }
        ));

        context().addPolicy(new InlineInjectionPolicy<>(
            HttpResponse.class,
            InjectionPolicy.ObjectLifespan.DISPOSABLE,
            (requestingContext, name, typeData) ->
            {
                HttpDispatchContext context = Cast.attempt(HttpDispatchContext.class, requestingContext)
                        .orElseThrow(() -> new InputException.PreconditionFailed("This action requires access through HTTP"));

                return context.getResponse();
            }
        ));
    }

    public final void dispatch (HttpRequest request, HttpResponse response)
    {
        dispatch(new HttpDispatchContext(context(), request, response));
    }
}
