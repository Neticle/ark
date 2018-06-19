package pt.neticle.ark.presentation;


import pt.neticle.ark.data.output.Output;
import pt.neticle.ark.exceptions.ImplementationException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The view proxy allows to quickly setup an instance that carries data in accordance with a defined interface.
 *
 * @param <TViewData>
 */
public class ViewProxy<TViewData extends ViewData>
{
    private final TViewData viewData;
    private final ViewObjectHandler handler;
    private final Map<Method, Object> bindings;

    private ViewProxy (Class<TViewData> viewDataClass)
    {
        bindings = new HashMap<>();
        handler = new ViewObjectHandler(bindings);
        viewData = (TViewData)Proxy.newProxyInstance(ViewProxy.class.getClassLoader(), new Class[]{ viewDataClass }, handler);
    }

    public <T> ViewProxy<TViewData> bind (Function<TViewData, T> prototype, T value)
    {
        prototype.apply(viewData);
        Method getter = handler.getLastInvocation();

        if(getter == null)
        {
            throw new ImplementationException("Provided binding prototype must be a method reference to a method of the proxied view class");
        }

        bindings.put(getter, value);

        return this;
    }

    public <TOut extends Output> View<TViewData, TOut> withRenderer (Function<TViewData, ViewRenderer<TOut>> renderer)
    {
        handler.enable();

        return new View<>(viewData, renderer);
    }

    public static <T extends ViewData> ViewProxy<T> of(Class<T> viewDataClass)
    {
        return new ViewProxy<>(viewDataClass);
    }

    private static class ViewObjectHandler implements InvocationHandler
    {
        private Method lastInvocation;
        private boolean recordInvocations;
        private final Map<Method, Object> bindings;

        public ViewObjectHandler (Map<Method, Object> bindings)
        {
            recordInvocations = true;
            this.bindings = bindings;
        }

        public void enable ()
        {
            recordInvocations = false;
        }

        public Method getLastInvocation ()
        {
            Method last = lastInvocation;
            lastInvocation = null;

            return last;
        }

        @Override
        public Object invoke (Object proxy, Method method, Object[] args) throws Throwable
        {
            if(recordInvocations)
            {
                lastInvocation = method;
                return null;
            }

            return bindings.get(method);
        }
    }
}
