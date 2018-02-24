package pt.neticle.ark.base;

import pt.neticle.ark.data.Converter;
import pt.neticle.ark.injection.InjectionPolicy;
import pt.neticle.ark.introspection.ArkTypeUtils;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the environment. See {@link pt.neticle.ark.base.Environment}.
 */
public class ApplicationEnvironment implements Environment
{
    private final Map<Class, InjectionPolicy> policies;
    private final Map<Class, Object> savedInstances;
    private final Converter ioConverter;

    public ApplicationEnvironment (Converter ioConverter)
    {
        this.policies = new ConcurrentHashMap<>();
        this.savedInstances = new ConcurrentHashMap<>();
        this.ioConverter = ioConverter;
    }

    public void addPolicy (InjectionPolicy policy)
    {
        policies.put(policy.getCriteriaType(), policy);
    }

    public Optional<InjectionPolicy> getPolicyFor(Class desiredType)
    {
        // TODO: Better / more efficient matching method
        // TODO: Take InjectionPolicy.Criteria into account when matching a suitable injector
        return Optional.ofNullable(policies.get(desiredType));
    }

    public <TDesired> Optional<TDesired> inject (Class<TDesired> desiredType, Context context,
                                                 Parameter parameter, ArkTypeUtils.ParameterType parameterType)
    {
        InjectionPolicy policy = getPolicyFor(desiredType).orElse(null);

        if(policy == null)
        {
            return Optional.empty();
        }

        Object instance;

        if(policy.getLifetime() == InjectionPolicy.ObjectLifetime.ONE_INSTANCE_PER_APPLICATION)
        {
            // Instances that are unique through out the application lifespan -- singletons -- should be stored in the environment
            instance = savedInstances.get(policy.getInjectedResultType());

            if(instance == null)
            {
                instance = policy.getInjector().inject(context, parameter, parameterType);
                savedInstances.put(policy.getInjectedResultType(), instance);
            }

            return Optional.of((TDesired)instance);
        }

        if(policy.getLifetime() == InjectionPolicy.ObjectLifetime.ONE_INSTANCE_PER_CONTEXT)
        {
            instance = context.getSavedInstance(policy.getInjectedResultType());

            if(instance == null)
            {
                instance = policy.getInjector().inject(context, parameter, parameterType);
                context.saveInstance(policy.getInjectedResultType(), instance);
            }

            return Optional.of((TDesired)instance);
        }

        // It is safe to do an unchecked cast because the policies are safely stored and retrieved together with their
        // respective types.
        return Optional.of((TDesired)policy.getInjector().inject(context, parameter, parameterType));
    }

    @Override
    public Converter getIOConverter ()
    {
        return ioConverter;
    }
}
