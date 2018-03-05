package pt.neticle.ark.injection;

import java.util.function.Supplier;

public class SingletonInjectionPolicy<TInjectedResult> extends InlineInjectionPolicy<TInjectedResult>
{
    public SingletonInjectionPolicy (Class<TInjectedResult> type, Supplier<TInjectedResult> injectorFn)
    {
        super(type, ObjectLifespan.DISPOSABLE, (requestingContext, name, typeData) -> injectorFn.get());
    }
}
