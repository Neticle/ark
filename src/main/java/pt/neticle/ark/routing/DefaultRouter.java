package pt.neticle.ark.routing;

import com.google.common.base.CaseFormat;
import com.google.common.collect.HashBasedTable;
import pt.neticle.ark.base.*;
import pt.neticle.ark.exceptions.ImplementationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The default router implementation. Maps routes as a concatenation of the controller's path and
 * the action's name in lower-hyphen format.
 */
public class DefaultRouter implements TwoWayRouter
{
    /**
     * Keys: path to action as text
     * Values: action handler
     */
    private final Map<String, ActionHandler> actionRoutes;

    /**
     * Keys: action handler
     * Values: path to action as text
     */
    private final Map<ActionHandler, String> routeActions;

    /**
     * Rows: controller class
     * Columns: action's real source name
     * Values: action handler
     *
     * Usage: Match action names to action handlers
     */
    private final HashBasedTable<Class, String, ActionHandler> reverseLookupTable;

    public DefaultRouter ()
    {
        actionRoutes = new HashMap<>();
        routeActions = new HashMap<>();
        reverseLookupTable = HashBasedTable.create();
    }

    @Override
    public void register (ActionHandler actionHandler)
    {
        String route = actionHandler.getControllerHandler().getPath();

        if(!route.startsWith("/"))
        {
            route = "/" + route;
        }

        if(!route.endsWith("/"))
        {
            route += "/";
        }

        // NOTE: Should probably validate controller path's format?

        route += sourceNameToPathName(actionHandler.getMethodName());

        actionRoutes.put(route, actionHandler);
        routeActions.put(actionHandler, route);
        reverseLookupTable.put(actionHandler.getControllerHandler().getControllerClass(), actionHandler.getMethodName(), actionHandler);
    }

    @Override
    public ActionHandler route (DispatchContext context)
    {
        return this.actionRoutes.get(context.getPath());
    }

    @Override
    public String pathTo (Class controller, String actionName)
    {
        ActionHandler matched = reverseLookupTable.get(controller, actionName);

        if(matched == null)
        {
            throw new ImplementationException.InvalidRoute(controller, actionName);
        }

        return routeActions.get(matched);
    }

    private String sourceNameToPathName (String sourceName)
    {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, sourceName);
    }
}
