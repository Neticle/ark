package pt.neticle.ark.base;

import pt.neticle.ark.data.Converter;
import pt.neticle.ark.data.input.Input;
import pt.neticle.ark.data.input.InputList;
import pt.neticle.ark.data.input.InputMap;
import pt.neticle.ark.data.input.OptionalInput;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.exceptions.InjectionException;
import pt.neticle.ark.injection.InjectionPolicy;
import pt.neticle.ark.injection.InlineInjectionPolicy;
import pt.neticle.ark.introspection.ArkTypeUtils;
import pt.neticle.ark.runtime.Cast;
import pt.neticle.ark.view.ViewTemplateResolver;

import java.util.Objects;
import java.util.function.Supplier;

public class ApplicationContext extends PolicyHoldingContext
{
    private Router router;
    private ReverseRouter reverseRouter;
    private Converter ioConverter;
    private ViewTemplateResolver viewTemplateResolver;

    public ApplicationContext (Context parent)
    {
        super(parent);

        try
        {
            initialize();
        } catch(InjectionException.NoSuitableInjector e)
        {
            throw new ImplementationException(e);
        }

        addPolicy(new InlineInjectionPolicy<>(
            Input.class,
            InjectionPolicy.ObjectLifespan.DISPOSABLE,
            (requestingContext, name, typeData) ->
            {
                Objects.requireNonNull(name, "Input injections require name specification");
                Objects.requireNonNull(typeData, "Input injections require type data");

                DispatchContext context = Cast.attempt(DispatchContext.class, requestingContext)
                    .orElseThrow(() -> new ImplementationException("Input injection requires a dispatch context"));

                ArkTypeUtils.ParameterType dataType = typeData.parameters().findFirst()
                    .orElseThrow(() -> new ImplementationException("Input parameter must specify underlying data type"));

                Converter ioConverter = context.inject(Converter.class, "io", null)
                    .orElseThrow(() -> new ImplementationException.InjectionFailed());

                return new Input<>(ioConverter, context, name, dataType.getType());
            }
        ));

        addPolicy(new InlineInjectionPolicy<>(
            OptionalInput.class,
            InjectionPolicy.ObjectLifespan.DISPOSABLE,
            (requestingContext, name, typeData) ->
            {
                Objects.requireNonNull(name, "Input injections require name specification");
                Objects.requireNonNull(typeData, "Input injections require type data");

                DispatchContext context = Cast.attempt(DispatchContext.class, requestingContext)
                    .orElseThrow(() -> new ImplementationException("Input injection requires a dispatch context"));

                ArkTypeUtils.ParameterType dataType = typeData.parameters().findFirst()
                    .orElseThrow(() -> new ImplementationException("Input parameter must specify underlying data type"));

                Converter ioConverter = context.inject(Converter.class, "io", null)
                    .orElseThrow(() -> new ImplementationException.InjectionFailed());

                return new OptionalInput<>(ioConverter, context, name, dataType.getType());
            }
        ));

        addPolicy(new InlineInjectionPolicy<>(
            InputList.class,
            InjectionPolicy.ObjectLifespan.DISPOSABLE,
            (requestingContext, name, typeData) ->
            {
                Objects.requireNonNull(name, "Input injections require name specification");
                Objects.requireNonNull(typeData, "Input injections require type data");

                DispatchContext context = Cast.attempt(DispatchContext.class, requestingContext)
                    .orElseThrow(() -> new ImplementationException("Input injection requires a dispatch context"));

                ArkTypeUtils.ParameterType dataType = typeData.parameters().findFirst()
                    .orElseThrow(() -> new ImplementationException("Input parameter must specify underlying data type"));

                Converter ioConverter = context.inject(Converter.class, "io", null)
                    .orElseThrow(() -> new ImplementationException.InjectionFailed());

                return new InputList<>(ioConverter, context, name, dataType.getType());
            }
        ));

        addPolicy(new InlineInjectionPolicy<>(
            InputMap.class,
            InjectionPolicy.ObjectLifespan.DISPOSABLE,
            (requestingContext, name, typeData) ->
            {
                Objects.requireNonNull(name, "Input injections require name specification");
                Objects.requireNonNull(typeData, "Input injections require type data");

                DispatchContext context = Cast.attempt(DispatchContext.class, requestingContext)
                    .orElseThrow(() -> new ImplementationException("Input injection requires a dispatch context"));

                ArkTypeUtils.ParameterType keyType = typeData.parameterAt(0)
                    .orElseThrow(() -> new ImplementationException("Input parameter must specify underlying key data type"));

                ArkTypeUtils.ParameterType valueType = typeData.parameterAt(1)
                    .orElseThrow(() -> new ImplementationException("Input parameter must specify underlying value data type"));

                Converter ioConverter = context.inject(Converter.class, "io", null)
                    .orElseThrow(() -> new ImplementationException.InjectionFailed());

                return new InputMap<>(ioConverter, context, name, keyType.getType(), valueType.getType());
            }
        ));

        addPolicy(new InlineInjectionPolicy<>(
            Converter.class,
            InjectionPolicy.ObjectLifespan.DISPOSABLE,
            (requestingContext, name, typeData) ->
            {
                if(name.equals("io"))
                {
                    return getIoConverter();
                }

                return null;
            }
        ));

        addPolicy(singletonPolicy(ReverseRouter.class, this::getReverseRouter));
    }

    private void initialize () throws InjectionException.NoSuitableInjector
    {
        router = inject(Router.class, null, null)
            .orElseThrow(() -> new ImplementationException("Unable to obtain router instance"));

        reverseRouter = (router instanceof ReverseRouter) ?
            (ReverseRouter) router :
            inject(ReverseRouter.class, null, null)
                .orElseThrow(() -> new ImplementationException("Unable to obtain reverse router instance"));

        ioConverter = inject(Converter.class, null, null)
            .orElseThrow(() -> new ImplementationException("Unable to obtain IO converter instance"));

        viewTemplateResolver = inject(ViewTemplateResolver.class, null, null)
            .orElseThrow(() -> new ImplementationException("Unable to obtain view template resolver instance"));
    }

    protected <T> InjectionPolicy singletonPolicy (Class<T> type, Supplier<T> supplier)
    {
        return new InlineInjectionPolicy<T>(type, InjectionPolicy.ObjectLifespan.DISPOSABLE, (requestingContext, name, typeData) -> supplier.get());
    }

    public Router getRouter ()
    {
        return router;
    }

    public ReverseRouter getReverseRouter ()
    {
        return reverseRouter;
    }

    public Converter getIoConverter ()
    {
        return ioConverter;
    }

    public ViewTemplateResolver getViewTemplateResolver ()
    {
        return viewTemplateResolver;
    }
}
