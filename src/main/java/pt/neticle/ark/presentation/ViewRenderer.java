package pt.neticle.ark.presentation;

import pt.neticle.ark.data.output.Output;

@FunctionalInterface
public interface ViewRenderer<T extends Output>
{
    T render ();
}
