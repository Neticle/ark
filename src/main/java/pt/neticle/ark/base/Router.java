package pt.neticle.ark.base;

/**
 * The router accepts existing action handlers and takes the necessary information
 * in order to compute and store the routes that lead up to each action.
 *
 * During the application runtime the router will be used to match paths to actions.
 */
public interface Router extends ReverseRouter
{
    void register (ActionHandler actionHandler);

    ActionHandler route (DispatchContext context);
}
