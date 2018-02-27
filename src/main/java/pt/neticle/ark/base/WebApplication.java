package pt.neticle.ark.base;

import pt.neticle.ark.data.Converter;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.exceptions.InputException;
import pt.neticle.ark.http.HttpDispatchContext;
import pt.neticle.ark.http.HttpRequest;
import pt.neticle.ark.http.HttpResponse;
import pt.neticle.ark.injection.InlineInjectionPolicy;
import pt.neticle.ark.runtime.Cast;
import pt.neticle.ark.view.ViewTemplateResolver;

public class WebApplication extends Application
{
    public WebApplication ()
    {
        super();
        configure();
    }

    public WebApplication (TwoWayRouter _router, Converter ioConverter, ViewTemplateResolver _viewTemplateResolver)
    {
        super(_router, ioConverter, _viewTemplateResolver);
        configure();
    }

    private void configure ()
    {
        environment.addPolicy(new InlineInjectionPolicy<>
        (
            HttpRequest.class,
            (context, param, paramType) ->
            {
                HttpDispatchContext _context = Cast.attempt(HttpDispatchContext.class, context)
                        .orElseThrow(() -> new InputException.PreconditionFailed("This action requires access through HTTP"));

                return _context.getRequest();
            }
        ));

        environment.addPolicy(new InlineInjectionPolicy<>
        (
            HttpResponse.class,
            (context, param, paramType) ->
            {
                HttpDispatchContext _context = Cast.attempt(HttpDispatchContext.class, context)
                        .orElseThrow(() -> new InputException.PreconditionFailed("This action requires access through HTTP"));

                return _context.getResponse();
            }
        ));
    }

    public final void dispatch (HttpRequest request, HttpResponse response)
    {
        dispatch(new HttpDispatchContext(this, request, response));
    }
}
