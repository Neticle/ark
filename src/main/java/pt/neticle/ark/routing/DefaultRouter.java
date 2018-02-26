package pt.neticle.ark.routing;

import com.google.common.base.CaseFormat;
import com.google.common.collect.HashBasedTable;
import pt.neticle.ark.base.*;
import pt.neticle.ark.exceptions.ImplementationException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The default router implementation. Maps routes as a concatenation of the controller's path and
 * the action's name in lower-hyphen format.
 */
public class DefaultRouter implements TwoWayRouter
{
    /**
     * Maps controller routes to lists of action routes
     */
    private final Map<Route<ControllerHandler>, List<Route<ActionHandler>>> routeTree;

    /**
     * Reverse lookup maps
     */
    private final Map<ControllerHandler, Route<ControllerHandler>> controllersToRoutes;
    private final Map<ActionHandler, Route<ActionHandler>> actionsToRoutes;

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
        routeTree = new HashMap<>();
        controllersToRoutes = new HashMap<>();
        actionsToRoutes = new HashMap<>();
        reverseLookupTable = HashBasedTable.create();
    }

    @Override
    public void register (ActionHandler actionHandler)
    {
        Route<ControllerHandler> controllerRoute = controllersToRoutes.get(actionHandler.getControllerHandler());

        if(controllerRoute == null)
        {
            controllerRoute = Route.forController(actionHandler.getControllerHandler().getPath(), actionHandler.getControllerHandler());
            routeTree.put(controllerRoute, new ArrayList<>());

            controllersToRoutes.put(actionHandler.getControllerHandler(), controllerRoute);
        }

        Route<ActionHandler> actionRoute = Route.forAction
        (
            actionHandler.getControllerHandler().getPath() +
                (actionHandler.getDefinedPath().length() > 0 ?
                    actionHandler.getDefinedPath() :
                    sourceNameToPathName(actionHandler.getMethodName())),

            actionHandler
        );

        routeTree.get(controllerRoute).add(actionRoute);
        actionsToRoutes.put(actionHandler, actionRoute);
        reverseLookupTable.put(actionHandler.getControllerHandler().getControllerClass(), actionHandler.getMethodName(), actionHandler);
    }

    @Override
    public ActionHandler route (DispatchContext context)
    {
        Route<ActionHandler> route = routeTree.entrySet().stream()
            .filter((controllerRt) -> controllerRt.getKey().matches(context.getPath())) // check controllers first
            .map((controllerRt) -> controllerRt.getValue().stream() // check matched controller's actions
                .filter(actionRt -> actionRt.matches(context.getPath()))
                .findFirst()
                .orElse(null)
            )
            .filter(a -> a != null)
            .findFirst().orElse(null);

        if(route == null)
        {
            return null;
        }

        for(Map.Entry<String,String> pathParameter : route.extractParameters(context.getPath()).entrySet())
        {
            context.getParameters().add(new DispatchContext.DispatchParameter(pathParameter.getKey(), pathParameter.getValue()));
        }

        return route.node;
    }

    @Override
    public String pathTo (Class controller, String actionName, String... pathParameters)
    {
        ActionHandler matched = reverseLookupTable.get(controller, actionName);

        if(matched == null)
        {
            throw new ImplementationException.InvalidRoute(controller, actionName);
        }

        return actionsToRoutes.get(matched).build(pathParameters);
    }

    private String sourceNameToPathName (String sourceName)
    {
        // Routes that don't declare a specific path use their method name as their path. This function converts the name.
        // "Read" would be "read"
        // "ReadPage" would be "read-page"

        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, sourceName);
    }

    private static class Route<TNode>
    {
        /**
         * The original, untouched, route as declared in the annotations
         */
        private final String declaration;

        /**
         * The declared route, with named path parameteres replaced with "{}"
         * Used to build routes based on given parameters.
         */
        private final String template;

        /**
         * The pattern for this given route
         */
        private final Pattern pattern;

        /**
         * List of parameter names, in order of appearance in the path
         */
        private final List<String> namedParameters;

        /**
         * Target node. At the moment it's either a controller or an action.
         */
        private final TNode node;

        private Route (String declaration, Pattern pattern, String[] parameters, TNode node)
        {
            this.declaration = declaration;
            this.pattern = pattern;
            this.namedParameters = Arrays.asList(parameters);
            this.node = node;

            this.template = declaration.replaceAll("(:[\\w]+)", "{}");
        }

        public boolean matches (String path)
        {
            return pattern.matcher(path).lookingAt();
        }

        /**
         * Extracts the path parameters to a map, keys being the parameter names and values being the provided
         * (in path) values.
         *
         * @param path
         * @return
         */
        public Map<String,String> extractParameters (String path)
        {
            HashMap<String,String> params = new HashMap<>();

            Matcher matcher = pattern.matcher(path);
            if(matcher.find())
            {
                for(int i = 0; i < namedParameters.size() && i < matcher.groupCount(); i++)
                {
                    params.put(namedParameters.get(i), matcher.group(i+1));
                }
            }

            return params;
        }

        /**
         * Builds a path string that matches this route. (Reverse-routing)
         *
         * If the route accepts path parameters, those need to be provided, in the correct order.
         *
         * @param pathParameters
         * @return
         */
        public String build (String[] pathParameters)
        {
            if(pathParameters.length != namedParameters.size())
            {
                throw new ImplementationException("Route " + this.toString() + " requires " + namedParameters.size() + " path parameters to be specified");
            }

            String result = template;
            for(int i = 0; i < pathParameters.length; i++)
            {
                result = result.replaceFirst("\\{\\}", pathParameters[i]);
            }

            return result;
        }

        @Override
        public String toString ()
        {
            return declaration;
        }

        public static Route<ControllerHandler> forController (String path, ControllerHandler handler)
        {
            return new Route<>(path, Pattern.compile("^" + Pattern.quote(path)), new String[0], handler);
        }

        public static Route<ActionHandler> forAction (String path, ActionHandler handler)
        {
            String pattern = "^\\/" + Arrays.stream(path.split("\\/"))
                .filter(p -> p.length() > 0)
                .map(p -> p.startsWith(":") ? "([\\w-]+)" : Pattern.quote(p))
                .collect(Collectors.joining("\\/")) + "\\/?$";

            String[] paramNames = Arrays.stream(path.split("\\/"))
                .filter(p -> p.startsWith(":"))
                .map(p -> p.substring(1))
                .toArray(String[]::new);

            return new Route<>(path, Pattern.compile(pattern), paramNames, handler);
        }
    }
}
