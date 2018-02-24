package pt.neticle.ark.injection;

import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.input.InputMap;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.introspection.ArkTypeUtils;
import pt.neticle.ark.runtime.Cast;

public class InputMapInjectionPolicy extends InlineInjectionPolicy<InputMap>
{
    public InputMapInjectionPolicy ()
    {
        super
        (
            InputMap.class,
            (context, param, paramType) ->
            {
                DispatchContext _context = Cast.attempt(DispatchContext.class, context)
                    .orElseThrow(() -> new ImplementationException("Input injection requires a dispatch context"));

                ArkTypeUtils.ParameterType keyType = paramType.parameterAt(0)
                    .orElseThrow(() -> new ImplementationException());

                ArkTypeUtils.ParameterType valueType = paramType.parameterAt(1)
                    .orElseThrow(() -> new ImplementationException());

                return new InputMap<>(_context, param.getName(), keyType.getType(), valueType.getType());
            }
        );
    }
}
