package pt.neticle.ark.base;

import pt.neticle.ark.data.Converter;
import pt.neticle.ark.data.DefaultConverter;
import pt.neticle.ark.injection.InjectionPolicy;
import pt.neticle.ark.injection.InlineInjectionPolicy;
import pt.neticle.ark.routing.DefaultRouter;
import pt.neticle.ark.view.DefaultViewTemplateResolver;
import pt.neticle.ark.view.ViewTemplateResolver;

import java.util.Optional;

public class DefaultMainContext extends PolicyHoldingContext
{
    public DefaultMainContext ()
    {
        super(null);

        addPolicy(new InlineInjectionPolicy<>(
            Router.class,
            InjectionPolicy.ObjectLifespan.RETAINED,
            (requestingContext, name, typeData) -> new DefaultRouter()
        ));

        addPolicy(new InlineInjectionPolicy<>(
            Converter.class,
            InjectionPolicy.ObjectLifespan.RETAINED,
            (requstingContext, name, typeData) -> new DefaultConverter()
        ));

        addPolicy(new InlineInjectionPolicy<>(
            ViewTemplateResolver.class,
            InjectionPolicy.ObjectLifespan.RETAINED,
            (requestingContext, name, typeData) -> new DefaultViewTemplateResolver()
        ));
    }

    @Override
    public Optional<InjectionPolicy> getPolicyFor (Class desiredType, Context requestingContext)
    {
        if(requestingContext.parent == this)
        {
            return super.getPolicyFor(desiredType, requestingContext);
        }

        return Optional.empty();
    }
}
