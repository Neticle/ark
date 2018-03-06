// Copyright 2018 Igor Azevedo <igor.azevedo@neticle.pt>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
