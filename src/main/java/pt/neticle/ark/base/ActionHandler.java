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

import pt.neticle.ark.annotations.Action;
import pt.neticle.ark.data.Pair;
import pt.neticle.ark.data.input.Input;
import pt.neticle.ark.data.input.OptionalInput;
import pt.neticle.ark.data.output.Output;
import pt.neticle.ark.exceptions.ImplementationException;
import pt.neticle.ark.introspection.ArkTypeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * An action handler object handles both descriptive data and invokation of controller actions.
 *
 * Each action method should have exactly one handler, which lasts the whole lifespan of the application.
 */
public class ActionHandler
{
    private static final Logger Log = Logger.getLogger(ActionHandler.class.getName());

    /**
     * The owning controller's handler
     */
    private final ControllerHandler controllerHandler;

    /**
     * The method's real name, as written in the source code
     */
    private final String methodName;

    /**
     * The associated annotation
     */
    private final Action annotation;

    /**
     * The underlying action method
     */
    private final Method method;

    /**
     * Holds both the Parameter object, which is a reflection object provided by java, with the ParameterType,
     * which is generated parsed by us and used to provide complete parameter type information at runtime.
     */
    private final List<Pair<Parameter, ArkTypeUtils.ParameterType>> parameters;

    /**
     * The method's return type information
     */
    private final ArkTypeUtils.ParameterType returnType;

    /**
     * Creates an action handler based on the provided method.
     *
     * @param parent The handler of the controller that owns this action
     * @param actionMethod The action's method
     */
    protected ActionHandler (ControllerHandler parent, Method actionMethod)
    {
        Log.fine(() -> "Creating action handler for " + parent.getControllerInstance().getClass().getName() + " " + actionMethod.toGenericString());

        controllerHandler = parent;
        methodName = actionMethod.getName();
        method = actionMethod;
        annotation = method.getAnnotation(Action.class);
        parameters = new ArrayList<>();

        // Because of type erasure, the type parameters (generics) of the method parameters won't be available at runtime.
        // To overcome this, we use the toGenericString() method which returns a string containing the method's complete
        // signature, including the complete type information.
        // ArkTypeUtils takes care of parsing that string and finding the Class<> objects for each specified parameter.
        // Because this only happens once -- at application startup -- any performance penalty here is negligible.
        ArkTypeUtils.ParametersList methodParameters = ArkTypeUtils.parseMethodParametersSignature(method.toGenericString())
            .orElseThrow(() -> new ImplementationException("Unable to parse method signature: " + method.toGenericString()));

        if(Output.class.isAssignableFrom(method.getReturnType()))
        {
            returnType = ArkTypeUtils.parseMethodReturnType(method.toGenericString())
                .orElseThrow(() -> new ImplementationException("Unable to parse method signature for return type: " + method.toGenericString()));
        }
        else
        {
            returnType = null;
        }

        try
        {
            // After parsing the signature, here we actually resolve all the type names from text to actual class objects

            if(returnType != null)
            {
                returnType.resolveType();
            }

            methodParameters.resolveTypes();
        } catch(ClassNotFoundException e)
        {
            throw new ImplementationException("Could not resolve type specified in action method signature", e);
        }

        List<ArkTypeUtils.ParameterType> parameterTypes = methodParameters.getParameters();

        if(parameterTypes.size() != method.getParameters().length)
        {
            throw new ImplementationException("Method parameters meta-data mismatch");
        }

        int i = 0;
        for(Parameter reflParam : method.getParameters())
        {
            Log.fine("Added action parameter " + reflParam.getName() + ": " + parameterTypes.get(i).toString());
            parameters.add(new Pair<>(reflParam, parameterTypes.get(i)));
            i++;
        }

        Log.info("Action handler for " + controllerHandler.getControllerClass().getName() + ":" + methodName + " created.");
    }

    /**
     * Prepares and dispatches this handler's action.
     *
     * @param context The application-provided context from which the dispatch was issued
     */
    protected Output<?> dispatch (DispatchContext context) throws Throwable
    {
        Log.fine("Entered action handler dispatch logic");

        // The array that will carry the method arguments
        Object[] parameterValues = new Object[parameters.size()];

        int i = 0;
        for(Pair<Parameter, ArkTypeUtils.ParameterType> parameter : parameters)
        {
            parameterValues[i] = context.inject(parameter.A.getType(), parameter.A.getName(), parameter.B)
                .orElseThrow(() -> new ImplementationException.InjectionFailed("Unable to inject value for parameter " + parameter.A.getName()));

            i++;
        }

        try
        {
            Object returnVal = method.invoke(this.controllerHandler.getControllerInstance(), parameterValues);

            return hasOutputReturnType() ? (Output)returnVal : null;

        } catch(IllegalAccessException e)
        {
            // Shouldn't happen since we only create handlers for public methods

            throw new ImplementationException(e);
        }
        catch(InvocationTargetException e)
        {
            // Pass-through any exceptions thrown within the action method
            throw e.getCause();
        }
    }

    public Stream<Pair<Parameter, ArkTypeUtils.ParameterType>> inputParameters ()
    {
        return parameters.stream()
            .filter(p ->
                Input.class.isAssignableFrom(p.A.getType()) ||
                OptionalInput.class.isAssignableFrom(p.A.getType()));
    }

    public Method getMethod ()
    {
        return method;
    }

    public Action getAnnotation ()
    {
        return annotation;
    }

    /**
     * Gets the action's method real name, as written on the source code.
     *
     * @return The method's name
     */
    public String getMethodName ()
    {
        return methodName;
    }

    public String getDefinedPath ()
    {
        return annotation.path();
    }

    /**
     * Gets this handler's owning controller's handler
     *
     * @return The owning controller's handler
     */
    public ControllerHandler getControllerHandler ()
    {
        return controllerHandler;
    }

    /**
     * Gets the type information for the declared return type of the action.
     *
     * Return type will only be populated if method returns an object derived of {@link pt.neticle.ark.data.output.Output},
     * otherwise any other types are treated the same as void and this method will return null.
     *
     * @return Type information about the method's return type, if available, null otherwise
     */
    public ArkTypeUtils.ParameterType getReturnType ()
    {
        return returnType;
    }

    /**
     * Checks if the method has a defined return type that derives of {@link pt.neticle.ark.data.output.Output}.
     *
     * @return
     */
    public boolean hasOutputReturnType ()
    {
        return returnType != null;
    }
}
