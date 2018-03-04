package pt.neticle.ark.base;

import pt.neticle.ark.exceptions.InjectionException;
import pt.neticle.ark.injection.InjectionPolicy;
import pt.neticle.ark.introspection.ArkTypeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The context serves the purpose of encapsulating various usages of the application.
 *
 * Derivatives of this class exist, this being the base and the most simplistic one.
 *
 * The context may be used to support background tasks. With it being the central object that
 * keeps track of resources and object instances and injections.
 *
 * For tasks other than background work that require more functionality and/or input/output
 * between a client and server, subclasses such as {@link pt.neticle.ark.base.DispatchContext}
 * and {@link pt.neticle.ark.http.HttpDispatchContext} exist.
 */
public abstract class Context
{
    /**
     * The parent context
     */
    protected final Context parent;

    /**
     * Whenever an object is injected, if it's injection policy states that there should be only one instance
     * of it per context, that instance will be stored here for future injections.
     */
    private final Map<Class, Object> savedInstances;

    /**
     * Creates a new context, given the creator application.
     *
     * @param parent The parent/creator context
     */
    public Context (Context parent)
    {
        this.parent = parent;
        this.savedInstances = new HashMap<>();
    }

    /**
     * Gets the application instance that originated this context
     *
     * @return The parent application
     */
    public Context getParent ()
    {
        return parent;
    }

    public Optional<InjectionPolicy> getPolicyFor (Class desiredType, Context requestingContext)
    {
        return parent != null ? parent.getPolicyFor(desiredType, requestingContext) : Optional.empty();
    }

    public <TDesired> Optional<TDesired> inject (Class<TDesired> desiredType,
                                                String name,
                                                ArkTypeUtils.ParameterType typeData,
                                                Context requestingContext) throws InjectionException.NoSuitableInjector
    {
        InjectionPolicy policy = getPolicyFor(desiredType, requestingContext)
            .orElseThrow(() -> new InjectionException.NoSuitableInjector(desiredType));

        Object instance = null;

        if(policy.getObjectLifespan() == InjectionPolicy.ObjectLifespan.RETAINED)
        {
            instance = savedInstances.computeIfAbsent(policy.getInjectedResultType(), (t) ->
                policy.getInjector().inject(requestingContext, name, typeData));
        }

        else if (policy.getObjectLifespan() == InjectionPolicy.ObjectLifespan.DISPOSABLE)
        {
            instance = policy.getInjector().inject(requestingContext, name, typeData);
        }

        return Optional.ofNullable((TDesired)instance);
    }

    public <TDesired> Optional<TDesired> inject (Class<TDesired> desiredType,
                                                 String name,
                                                 ArkTypeUtils.ParameterType typeData) throws InjectionException.NoSuitableInjector
    {
        return inject(desiredType, name, typeData, this);
    }
}
