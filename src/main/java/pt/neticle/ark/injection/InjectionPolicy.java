package pt.neticle.ark.injection;

import java.lang.reflect.Parameter;

public class InjectionPolicy<TCriteria, TInjectedResult>
{
    public enum Criteria
    {
        /**
         * Object is candidate only when injection requires it's specific class
         */
        SPECIFIC_CLASS,

        /**
         * Object is candidate when injection requires it's base class
         */
        BASE_CLASS,

        /**
         * Object is candidate when an interface it implements is required for injection
         */
        INTERFACE
    }

    public enum ObjectLifetime
    {
        ONE_INSTANCE_PER_APPLICATION,
        ONE_INSTANCE_PER_CONTEXT,
        ALWAYS_NEW_INSTANCE
    }

    private final Class<TCriteria> criteriaType;
    private final Class<TInjectedResult> injectedResultType;
    private final Criteria criteria;
    private final ObjectLifetime lifetime;
    private final Injector<TInjectedResult> injector;

    public InjectionPolicy (Class<TCriteria> criteriaType, Class<TInjectedResult> injectedResultType, Criteria criteria, ObjectLifetime lifetime, Injector<TInjectedResult> injector)
    {
        this.criteriaType = criteriaType;
        this.injectedResultType = injectedResultType;
        this.criteria = criteria;
        this.lifetime = lifetime;
        this.injector = injector;
    }

    public boolean matchesRequiredType (Parameter parameter)
    {
        if(criteria == Criteria.SPECIFIC_CLASS)
        {
            return parameter.getType().equals(criteriaType);
        }

        if(criteria == Criteria.BASE_CLASS)
        {
            return parameter.getType().isAssignableFrom(criteriaType);
        }

        if(criteria == Criteria.INTERFACE)
        {
            for(Class ifc : criteriaType.getInterfaces())
            {
                if(ifc.equals(parameter.getType()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public Injector<TInjectedResult> getInjector ()
    {
        return injector;
    }

    public Class<TCriteria> getCriteriaType ()
    {
        return criteriaType;
    }

    public Class<TInjectedResult> getInjectedResultType ()
    {
        return injectedResultType;
    }

    public Criteria getCriteria ()
    {
        return criteria;
    }

    public ObjectLifetime getLifetime ()
    {
        return lifetime;
    }
}
