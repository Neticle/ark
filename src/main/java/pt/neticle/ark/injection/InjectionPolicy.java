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

package pt.neticle.ark.injection;

public class InjectionPolicy<TInjectedResult>
{
    public enum Criteria
    {
        /**
         * Object is candidate only when injection requires it's specific class
         */
        SPECIFIC,

        /**
         * Object is candidate when injection requires it's base class
         */
        BASE
    }

    public enum ObjectLifespan
    {
        RETAINED,
        DISPOSABLE
    }

    private final Class<?> criteriaType;
    private final Class<TInjectedResult> injectedResultType;
    private final Criteria criteria;
    private final ObjectLifespan lifespan;
    private final Injector<TInjectedResult> injector;

    public InjectionPolicy (Class<?> criteriaType, Class<TInjectedResult> injectedResultType, Criteria criteria, ObjectLifespan lifespan, Injector<TInjectedResult> injector)
    {
        this.criteriaType = criteriaType;
        this.injectedResultType = injectedResultType;
        this.criteria = criteria;
        this.lifespan = lifespan;
        this.injector = injector;
    }

    public boolean matchesRequiredType (Class<?> required)
    {
        if(criteria == Criteria.SPECIFIC)
        {
            return required == criteriaType;
        }

        else if (criteria == Criteria.BASE)
        {
            return required.isAssignableFrom(criteriaType);
        }

        return false;
    }

    public Injector<TInjectedResult> getInjector ()
    {
        return injector;
    }

    public Class<?> getCriteriaType ()
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

    public ObjectLifespan getObjectLifespan ()
    {
        return lifespan;
    }
}
