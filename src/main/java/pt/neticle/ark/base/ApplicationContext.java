// Copyright 2018 Igor Azevedo <igor.azevedo@neticle.pt>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package pt.neticle.ark.base;

import pt.neticle.ark.data.Converter;
import pt.neticle.ark.data.input.Input;
import pt.neticle.ark.data.input.InputList;
import pt.neticle.ark.data.input.InputMap;
import pt.neticle.ark.data.input.OptionalInput;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.exceptions.InjectionException;
import pt.neticle.ark.failsafe.ErrorHandler;
import pt.neticle.ark.failsafe.InternalErrorHandler;
import pt.neticle.ark.http.HttpDispatchContext;
import pt.neticle.ark.injection.InjectionPolicy;
import pt.neticle.ark.injection.InlineInjectionPolicy;
import pt.neticle.ark.introspection.ArkTypeUtils;
import pt.neticle.ark.runtime.Cast;
import pt.neticle.ark.view.ViewTemplateResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ApplicationContext extends PolicyHoldingContext
{
    private static final Logger Log = Logger.getLogger(ApplicationContext.class.getName());

    private Router router;
    private ReverseRouter reverseRouter;
    private Converter ioConverter;
    private ViewTemplateResolver viewTemplateResolver;
    private ErrorHandler<DispatchContext> fallbackErrorHandler;
    private InternalErrorHandler<DispatchContext> fallbackInternalErrorHandler;
    private final Map<Class<? extends DispatchContext>, ErrorHandler<? extends DispatchContext>> errorHandlers;
    private final Map<Class<? extends DispatchContext>, InternalErrorHandler<? extends DispatchContext>> internalErrorHandlers;

    public ApplicationContext (Context parent)
    {
        super(parent);

        Log.fine(() -> "Creating application context");

        errorHandlers = new HashMap<>();
        internalErrorHandlers = new HashMap<>();

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

        Log.info(() -> "Application context has been created");
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

        fallbackErrorHandler = inject(ErrorHandler.class, null, new ArkTypeUtils.ParameterType(ErrorHandler.class, DispatchContext.class))
            .orElseThrow(() -> new ImplementationException("Unable to obtain fallback error handler instance"));

        fallbackInternalErrorHandler = inject(InternalErrorHandler.class, null, new ArkTypeUtils.ParameterType(InternalErrorHandler.class, DispatchContext.class))
                .orElseThrow(() -> new ImplementationException("Unable to obtain fallback error handler instance"));

        errorHandlers.put
        (
            HttpDispatchContext.class,
            inject(ErrorHandler.class, null, new ArkTypeUtils.ParameterType(ErrorHandler.class, HttpDispatchContext.class))
                    .orElseThrow(() -> new ImplementationException("Unable to obtain http error handler instance"))
        );

        internalErrorHandlers.put
        (
            HttpDispatchContext.class,
            inject(InternalErrorHandler.class, null, new ArkTypeUtils.ParameterType(InternalErrorHandler.class, HttpDispatchContext.class))
                    .orElseThrow(() -> new ImplementationException("Unable to obtain http internal error handler instance"))
        );
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

    public ErrorHandler<DispatchContext> getFallbackErrorHandler ()
    {
        return fallbackErrorHandler;
    }

    public InternalErrorHandler<DispatchContext> getFallbackInternalErrorHandler ()
    {
        return fallbackInternalErrorHandler;
    }

    public <T extends DispatchContext> ErrorHandler<T> getErrorHandlerFor (Class<T> contextType)
    {
        return (ErrorHandler<T>) errorHandlers.getOrDefault(contextType, fallbackErrorHandler);
    }

    public <T extends DispatchContext> InternalErrorHandler<T> getInternalErrorHandlerFor (Class<T> contextType)
    {
        return (InternalErrorHandler<T>) internalErrorHandlers.getOrDefault(contextType, fallbackInternalErrorHandler);
    }
}
