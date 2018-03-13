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
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.failsafe.ErrorHandler;
import pt.neticle.ark.failsafe.InternalErrorHandler;
import pt.neticle.ark.failsafe.handlers.DefaultWebErrorHandler;
import pt.neticle.ark.failsafe.handlers.FallbackErrorHandler;
import pt.neticle.ark.http.HttpDispatchContext;
import pt.neticle.ark.injection.InjectionPolicy;
import pt.neticle.ark.injection.InlineInjectionPolicy;
import pt.neticle.ark.introspection.ArkTypeUtils;
import pt.neticle.ark.routing.DefaultRouter;
import pt.neticle.ark.view.DefaultViewTemplateResolver;
import pt.neticle.ark.view.ViewTemplateResolver;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class MainContext extends PolicyHoldingContext
{
    private static final Logger Log = Logger.getLogger(MainContext.class.getName());

    protected MainContext (Supplier<Router> routerSupplier, Supplier<Converter> converterSupplier,
                           Supplier<ViewTemplateResolver> viewTemplateResolverSupplier,
                           Supplier<ErrorHandler<HttpDispatchContext>> webErrorHandler,
                           Supplier<InternalErrorHandler<HttpDispatchContext>> webInternalErrorHandler)
    {
        super(null);

        Log.fine(() -> "Creating main context");

        addPolicy(new InlineInjectionPolicy<>(
            Router.class,
            InjectionPolicy.ObjectLifespan.RETAINED,
            (requestingContext, name, typeData) -> routerSupplier.get()
        ));

        addPolicy(new InlineInjectionPolicy<>(
            Converter.class,
            InjectionPolicy.ObjectLifespan.RETAINED,
            (requstingContext, name, typeData) -> converterSupplier.get()
        ));

        addPolicy(new InlineInjectionPolicy<>(
            ViewTemplateResolver.class,
            InjectionPolicy.ObjectLifespan.RETAINED,
            (requestingContext, name, typeData) -> viewTemplateResolverSupplier.get()
        ));

        addPolicy(new InlineInjectionPolicy<>(
            ErrorHandler.class,
            InjectionPolicy.ObjectLifespan.DISPOSABLE,
            (requestingContext, name, typeData) ->
            {
                ArkTypeUtils.ParameterType contextType = typeData.parameterAt(0)
                    .orElseThrow(ImplementationException::new);

                if(HttpDispatchContext.class.isAssignableFrom(contextType.getType()))
                {
                    return webErrorHandler.get();
                }

                return new FallbackErrorHandler();
            }
        ));

        addPolicy(new InlineInjectionPolicy<>(
            InternalErrorHandler.class,
            InjectionPolicy.ObjectLifespan.DISPOSABLE,
            (requestingContext, name, typeData) ->
            {
                ArkTypeUtils.ParameterType contextType = typeData.parameterAt(0)
                        .orElseThrow(ImplementationException::new);

                if(HttpDispatchContext.class.isAssignableFrom(contextType.getType()))
                {
                    return webInternalErrorHandler.get();
                }

                return new FallbackErrorHandler();
            }
        ));

        Log.info(() -> "Main context has been created");
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
