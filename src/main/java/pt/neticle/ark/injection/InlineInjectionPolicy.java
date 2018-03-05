package pt.neticle.ark.injection;

import pt.neticle.ark.base.Context;
import pt.neticle.ark.introspection.ArkTypeUtils;

import java.lang.reflect.Parameter;

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
