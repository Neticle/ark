package pt.neticle.ark.presentation;

import pt.neticle.ark.data.output.Output;

import java.util.function.Function;

public class View<T extends ViewData, TOut extends Output> implements Output<View>
{
    private final T data;
    private final Function<T, ViewRenderer<TOut>> renderer;

    public View (T data, Function<T, ViewRenderer<TOut>> renderer)
    {
        this.data = data;
        this.renderer = renderer;
    }

    public TOut generateOutput ()
    {
        return renderer.apply(data).render();
    }
}
