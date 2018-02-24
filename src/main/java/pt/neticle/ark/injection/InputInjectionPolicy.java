package pt.neticle.ark.injection;

import pt.neticle.ark.base.Context;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.input.Input;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.introspection.ArkTypeUtils;

import java.lang.reflect.Parameter;

public class InputInjectionPolicy extends InjectionPolicy<Input, Input>
{
    public InputInjectionPolicy ()
    {
        super(Input.class, Input.class, Criteria.SPECIFIC_CLASS, ObjectLifetime.ALWAYS_NEW_INSTANCE, new InputInjector());
    }

    public static class InputInjector implements Injector<Input>
    {
        @Override
        public Input inject (Context context, Parameter parameter, ArkTypeUtils.ParameterType parameterType)
        {
            DispatchContext dContext;

            if(DispatchContext.class.isAssignableFrom(context.getClass()))
            {
                dContext = (DispatchContext)context;
            }
            else
            {
                throw new ImplementationException("Input injection requires a dispatch context");
            }

            ArkTypeUtils.ParameterType dataType = parameterType.parameters().findFirst()
                .orElseThrow(() -> new ImplementationException("Input parameter must specify underlying data type"));

            return new Input<>(dContext, parameter.getName(), dataType.getType());
        }

        @Override
        public void cleanup (Context context, Input object)
        {
            // do nothing
        }
    }
}
