package pt.neticle.ark.injection;

import pt.neticle.ark.base.Context;
import pt.neticle.ark.introspection.ArkTypeUtils;

import java.lang.reflect.Parameter;

public interface Injector<TInjectedResult>
{
    TInjectedResult inject (Context requestingContext, String name, ArkTypeUtils.ParameterType typeData);
    void cleanup(Context context, TInjectedResult object);
}
