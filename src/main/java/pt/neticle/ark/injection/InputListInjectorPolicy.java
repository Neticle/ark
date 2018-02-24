package pt.neticle.ark.injection;

import pt.neticle.ark.base.Context;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.input.InputList;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.introspection.ArkTypeUtils;
import pt.neticle.ark.runtime.Cast;
import java.lang.reflect.Parameter;

public class InputListInjectorPolicy extends InjectionPolicy<InputList, InputList>
{
    public InputListInjectorPolicy ()
    {
        super(InputList.class, InputList.class, Criteria.SPECIFIC_CLASS, ObjectLifetime.ALWAYS_NEW_INSTANCE, new InputListInjector());
    }

    private static class InputListInjector implements Injector<InputList>
    {
        @Override
        public InputList inject (Context context, Parameter parameter, ArkTypeUtils.ParameterType parameterType)
        {
            DispatchContext _context = Cast.attempt(DispatchContext.class, context)
                .orElseThrow(() -> new ImplementationException.InjectionFailed("Input injection requires a dispatch context"));

            ArkTypeUtils.ParameterType dataType = parameterType.parameters().findFirst()
                .orElseThrow(() -> new ImplementationException("OptionalInput parameter must specify underlying data type"));

            return new InputList<>(_context, parameter.getName(), dataType.getType());
        }

        @Override
        public void cleanup (Context context, InputList object)
        {

        }
    }
}
