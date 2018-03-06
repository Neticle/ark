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

import pt.neticle.ark.base.Context;
import pt.neticle.ark.introspection.ArkTypeUtils;

public class InlineInjectionPolicy<T> extends InjectionPolicy<T>
{
    public InlineInjectionPolicy (Class<T> type, InjectorFunction<T> injectorFn)
    {
        this(type, ObjectLifespan.DISPOSABLE, injectorFn, (context, object) -> {});
    }

    public InlineInjectionPolicy (Class<T> type, InjectorFunction<T> injectorFn, CleanupFunction<T> cleanupFn)
    {
        this(type, ObjectLifespan.DISPOSABLE, injectorFn, cleanupFn);
    }

    public InlineInjectionPolicy (Class<T> type, ObjectLifespan lifetime, InjectorFunction<T> injectorFn)
    {
        this(type, lifetime, injectorFn, (context, object) -> {});
    }

    public InlineInjectionPolicy (Class<T> type, ObjectLifespan lifetime, InjectorFunction<T> injectorFn, CleanupFunction<T> cleanupFn)
    {
        super(type, type, Criteria.SPECIFIC, lifetime, new FunctionalInjector<T>(injectorFn, cleanupFn));
    }

    private static class FunctionalInjector<T> implements Injector<T>
    {
        private final InjectorFunction<T> injectorFn;
        private final CleanupFunction<T> cleanupFn;

        public FunctionalInjector(InjectorFunction<T> injectorFn, CleanupFunction<T> cleanupFn)
        {
            this.injectorFn = injectorFn;
            this.cleanupFn = cleanupFn;
        }

        @Override
        public T inject (Context context, String name, ArkTypeUtils.ParameterType parameterType)
        {
            return injectorFn.inject(context, name, parameterType);
        }

        @Override
        public void cleanup (Context context, T object)
        {
            cleanupFn.cleanup(context, object);
        }
    }

    @FunctionalInterface
    public interface InjectorFunction<T>
    {
        T inject(Context context, String name, ArkTypeUtils.ParameterType typeInfo);
    }

    @FunctionalInterface
    public interface CleanupFunction<T>
    {
        void cleanup(Context context, T object);
    }
}
