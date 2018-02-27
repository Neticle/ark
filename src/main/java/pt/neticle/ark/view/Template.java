package pt.neticle.ark.view;

import pt.neticle.ark.base.ActionHandler;
import pt.neticle.ark.base.DispatchContext;
import pt.neticle.ark.data.output.ContentOutput;

/**
 * A template contains the logic necessary to render a piece of content, given a set of input data.
 *
 * Template objects are meant to be initialized once and kept through-out the application's life-cycle,
 * so each template object will fulfill multiple responses and thus should not store any context-related
 * data on it's instance.
 */
public interface Template
{
    ContentOutput<?> render (DispatchContext context, ActionHandler origin, View view);
}
