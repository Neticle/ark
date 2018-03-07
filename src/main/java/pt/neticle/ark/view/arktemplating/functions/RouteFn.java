package pt.neticle.ark.view.arktemplating.functions;

import pt.neticle.ark.base.ReverseRouter;
import pt.neticle.ark.templating.exception.RenderingException;
import pt.neticle.ark.templating.structure.functions.DefaultFunctionHandler;

import java.util.Arrays;

public class RouteFn extends DefaultFunctionHandler<String>
{
    private final ReverseRouter router;

    public RouteFn (ReverseRouter router)
    {
        this.router = router;
    }

    @Override
    public String getName ()
    {
        return "Route";
    }

    @Override
    public String apply (Object[] args) throws RenderingException
    {
        if(args.length > 2)
        {
            String[] routeParams = Arrays.stream(args, 2, args.length)
                .filter((a) -> a != null)
                .map((a) -> a.toString())
                .toArray(String[]::new);

            try
            {
                return router.pathTo
                (
                    Class.forName(argument(args, 0, String.class, false)),
                    argument(args, 1, String.class),
                    routeParams
                );
            } catch(ClassNotFoundException e)
            {
                throw new RenderingException(e);
            }
        }

        ensureSignatureArgs(args, false, String.class, String.class);

        try
        {
            return router.pathTo(Class.forName(argument(args, 0, String.class, false)), argument(args, 1, String.class, false));
        } catch(ClassNotFoundException e)
        {
            throw new RenderingException(e);
        }
    }
}
