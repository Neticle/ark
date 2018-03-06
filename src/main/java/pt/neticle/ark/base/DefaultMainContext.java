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
