package pt.neticle.ark.view;

import pt.neticle.ark.data.output.Output;

import java.util.Map;

/**
 * A view object is essentially a container. It doesn't render anything, it just carries information necessary to
 * generate output.
 *
 * The name refers to the name of the template we're using. The data is just a map of key/values to be passed to the
 * template.
 *
 * Templates are resolved not just based on name but also on origin of the view.
 */
public interface View extends Output<View>
{
    String getName ();

    Map<String,Object> getData ();
}
