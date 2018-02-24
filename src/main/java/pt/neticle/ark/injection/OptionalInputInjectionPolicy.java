package pt.neticle.ark.injection;

import pt.neticle.ark.base.Context;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.input.OptionalInput;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.introspection.ArkTypeUtils;
import pt.neticle.ark.runtime.Cast;

import java.lang.reflect.Parameter;

public class OptionalInputInjectionPolicy extends InjectionPolicy
{
    public OptionalInputInjectionPolicy ()
    {
        super(OptionalInput.class, OptionalInput.class, Criteria.SPECIFIC_CLASS, ObjectLifetime.ALWAYS_NEW_INSTANCE, new OptionalInputInjector());
    }

    private static class OptionalInputInjector implements Injector<OptionalInput>
    {
        @Override
        public OptionalInput inject (Context context, Parameter parameter, ArkTypeUtils.ParameterType parameterType)
        {
            DispatchContext _context = Cast.attempt(DispatchContext.class, context)
                .orElseThrow(() -> new ImplementationException.InjectionFailed("Input injection requires a dispatch context"));

            ArkTypeUtils.ParameterType dataType = parameterType.parameters().findFirst()
                .orElseThrow(() -> new ImplementationException("OptionalInput parameter must specify underlying data type"));

            return new OptionalInput<>(_context, parameter.getName(), dataType.getType());
        }

        @Override
        public void cleanup (Context context, OptionalInput object)
        {

        }
    }
}
