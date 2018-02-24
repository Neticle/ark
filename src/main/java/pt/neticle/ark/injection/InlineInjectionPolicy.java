package pt.neticle.ark.injection;

import pt.neticle.ark.base.Context;
import pt.neticle.ark.introspection.ArkTypeUtils;

import java.lang.reflect.Parameter;

public class InlineInjectionPolicy<T> extends InjectionPolicy<T,T>
{
    public InlineInjectionPolicy (Class<T> type, InjectorFunction<T> injectorFn)
    {
        this(type, ObjectLifetime.ALWAYS_NEW_INSTANCE, injectorFn, (context, object) -> {});
    }

    public InlineInjectionPolicy (Class<T> type, InjectorFunction<T> injectorFn, CleanupFunction<T> cleanupFn)
    {
        this(type, ObjectLifetime.ALWAYS_NEW_INSTANCE, injectorFn, cleanupFn);
    }

    public InlineInjectionPolicy (Class<T> type, ObjectLifetime lifetime, InjectorFunction<T> injectorFn)
    {
        this(type, lifetime, injectorFn, (context, object) -> {});
    }

    public InlineInjectionPolicy (Class<T> type, ObjectLifetime lifetime, InjectorFunction<T> injectorFn, CleanupFunction<T> cleanupFn)
    {
        super(type, type, Criteria.SPECIFIC_CLASS, lifetime, new FunctionalInjector<T>(injectorFn, cleanupFn));
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
        public T inject (Context context, Parameter parameter, ArkTypeUtils.ParameterType parameterType)
        {
            return injectorFn.inject(context, parameter, parameterType);
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
        T inject(Context context, Parameter parameter, ArkTypeUtils.ParameterType parameterType);
    }

    @FunctionalInterface
    public interface CleanupFunction<T>
    {
        void cleanup(Context context, T object);
    }
}
