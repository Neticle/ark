package pt.neticle.ark.injection;

import pt.neticle.ark.base.Context;
import pt.neticle.ark.introspection.ArkTypeUtils;

import java.lang.reflect.Parameter;

public interface Injector<TInjectedResult>
{
    TInjectedResult inject(Context context, Parameter parameter, ArkTypeUtils.ParameterType parameterType);
    void cleanup(Context context, TInjectedResult object);
}
