package pt.neticle.ark.base;

import pt.neticle.ark.injection.InjectionPolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PolicyHoldingContext extends Context
{
    private final Map<Class, InjectionPolicy> policies;

    public PolicyHoldingContext (Context parent)
    {
        super(parent);

        policies = new HashMap<>();
    }

    public void addPolicy (InjectionPolicy policy)
    {
        policies.put(policy.getCriteriaType(), policy);
    }

    @Override
    public Optional<InjectionPolicy> getPolicyFor (Class desiredType, Context requestingContext)
    {
        InjectionPolicy policy = policies.get(desiredType);

        if(policy != null && policy.matchesRequiredType(desiredType))
        {
            return Optional.of(policy);
        }

        if(parent != null)
        {
            return parent.getPolicyFor(desiredType, requestingContext);
        }

        return Optional.empty();
    }
}
