package pt.neticle.ark.base;

import pt.neticle.ark.data.Converter;
import pt.neticle.ark.data.DefaultConverter;
import pt.neticle.ark.failsafe.ErrorHandler;
import pt.neticle.ark.failsafe.InternalErrorHandler;
import pt.neticle.ark.failsafe.handlers.DefaultWebErrorHandler;
import pt.neticle.ark.http.HttpDispatchContext;
import pt.neticle.ark.routing.DefaultRouter;
import pt.neticle.ark.view.DefaultViewTemplateResolver;
import pt.neticle.ark.view.ViewTemplateResolver;
import pt.neticle.ark.view.arktemplating.ArkTemplatingResolver;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultMainContext extends MainContext
{
    private static final Supplier<Router> defaultRouterSupplier
            = DefaultRouter::new;

    private static final Supplier<Converter> defaultConverterSupplier
            = DefaultConverter::new;

    private static final Supplier<ViewTemplateResolver> defaultViewTemplateResolverSupplier = () ->
    {
        Class<?> te = null;

        try
        {
            te = Class.forName("pt.neticle.ark.templating.TemplatingEngine");
        } catch(ClassNotFoundException e) { }

        return te == null ?
            new DefaultViewTemplateResolver() :
            new ArkTemplatingResolver();
    };

    private static final Supplier<ErrorHandler<HttpDispatchContext>> defaultWebErrorHandler
            = DefaultWebErrorHandler::new;

    private static final Supplier<InternalErrorHandler<HttpDispatchContext>> defaultWebInternalErrorHandler
            = DefaultWebErrorHandler::new;

    public DefaultMainContext ()
    {
        super(defaultRouterSupplier, defaultConverterSupplier, defaultViewTemplateResolverSupplier,
                defaultWebErrorHandler, defaultWebInternalErrorHandler);
    }

    protected DefaultMainContext (Supplier<Router> routerSupplier, Supplier<Converter> converterSupplier,
        Supplier<ViewTemplateResolver> viewTemplateResolverSupplier, Supplier<ErrorHandler<HttpDispatchContext>> webErrorHandler,
        Supplier<InternalErrorHandler<HttpDispatchContext>> webInternalErrorHandler)
    {
        super(routerSupplier, converterSupplier, viewTemplateResolverSupplier, webErrorHandler, webInternalErrorHandler);
    }

    public static class Builder
    {
        private Supplier<Router> routerSupplier = defaultRouterSupplier;
        private Supplier<Converter> converterSupplier = defaultConverterSupplier;
        private Supplier<ViewTemplateResolver> viewTemplateResolverSupplier = defaultViewTemplateResolverSupplier;
        private Supplier<ErrorHandler<HttpDispatchContext>> webErrorHandler = defaultWebErrorHandler;
        private Supplier<InternalErrorHandler<HttpDispatchContext>> webInternalErrorHandler = defaultWebInternalErrorHandler;

        private Consumer<DefaultMainContext> customInitializator = null;

        private Builder()
        {
        }

        public DefaultMainContext build ()
        {
            DefaultMainContext mc = new DefaultMainContext(routerSupplier, converterSupplier, viewTemplateResolverSupplier,
                webErrorHandler, webInternalErrorHandler);

            if(customInitializator != null)
            {
                customInitializator.accept(mc);
            }

            return mc;
        }

        public Builder withRouterSupplier (Supplier<Router> routerSupplier)
        {
            this.routerSupplier = routerSupplier;
            return this;
        }

        public Builder withConverterSupplier (Supplier<Converter> converterSupplier)
        {
            this.converterSupplier = converterSupplier;
            return this;
        }

        public Builder withViewTemplateResolverSupplier (Supplier<ViewTemplateResolver> viewTemplateResolverSupplier)
        {
            this.viewTemplateResolverSupplier = viewTemplateResolverSupplier;
            return this;
        }

        public Builder withWebErrorHandlers (Supplier<ErrorHandler<HttpDispatchContext>> webErrorHandler, Supplier<InternalErrorHandler<HttpDispatchContext>> webInternalErrorHandler)
        {
            this.webErrorHandler = webErrorHandler;
            this.webInternalErrorHandler = webInternalErrorHandler;
            return this;
        }

        public Builder withCustomInitialization (Consumer<DefaultMainContext> customInitializator)
        {
            this.customInitializator = customInitializator;
            return this;
        }
    }

    public static final Builder builder ()
    {
        return new Builder();
    }
}
