package pt.neticle.ark.ssrhtml.elements;

import pt.neticle.ark.data.MediaType;
import pt.neticle.ark.ssrhtml.Attribute;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Attributes
{
    public enum CrossoriginPolicy
    {
        /**
         * A cross-origin request (i.e. with Origin: HTTP header) is performed. But no credential is sent (i.e. no
         * cookie, no X.509 certificate and no HTTP Basic authentication). If the server does not give credentials
         * to the origin site (by not setting the Access-Control-Allow-Origin: HTTP header) the image will be tainted
         * and its usage restricted.
         */
        ANONYMOUS,

        /**
         * A cross-origin request (i.e. with Origin: HTTP header) is performed with credential is sent (i.e. a cookie,
         * a certificate and HTTP Basic authentication is performed). If the server does not give credentials to the
         * origin site (through Access-Control-Allow-Credentials: HTTP header), the image will be tainted and its
         * usage restricted.
         */
        USE_CREDENTIALS;

        @Override
        public String toString ()
        {
            switch(this)
            {
                case ANONYMOUS:
                    return "anonymous";
                case USE_CREDENTIALS:
                    return "use-credentials";
            }

            return "";
        }
    }

    public enum ReferrerPolicy
    {
        /**
         * Meaning that the Referer header will not be sent.
         */
        NO_REFERRER,

        /**
         * Meaning that no Referer header will be sent when navigating to an origin without TLS (HTTPS).
         * This is a user agent’s default behavior, if no policy is otherwise specified.
         */
        NO_REFFERER_WHEN_DOWNGRADE,

        /**
         * Meaning that the referrer will be the origin of the page, that is roughly the scheme, the host and the port.
         */
        ORIGIN,

        /**
         * Meaning that navigating to other origins will be limited to the scheme, the host and the port, while
         * navigating on the same origin will include the referrer's path.
         */
        ORIGIN_WHEN_CROSS_ORIGIN,

        /**
         * Meaning that the referrer will include the origin and the path (but not the fragment, password, or username).
         * This case is unsafe because it can leak origins and paths from TLS-protected resources to insecure origins.
         */
        UNSAFE_URL;

        @Override
        public String toString ()
        {
            switch(this)
            {
                case NO_REFERRER:
                    return "no-referrer";
                case NO_REFFERER_WHEN_DOWNGRADE:
                    return "no-referrer-when-downgrade";
                case ORIGIN:
                    return "origin";
                case ORIGIN_WHEN_CROSS_ORIGIN:
                    return "origin-when-cross-origin";
                case UNSAFE_URL:
                    return "unsafe-url";
            }

            return "";
        }
    }

    /**
     * A hint to the browser for which virtual keyboard to display. This attribute applies when the value of the type
     * attribute is text, password, email, or url.
     *
     * This is mostly used by mobile browsers.
     */
    public enum InputMode
    {
        /**
         * No virtual keyboard should be displayed.
         */
        NONE,

        /**
         * Text input in the user's locale.
         */
        TEXT,

        /**
         * Fractional numeric input.
         */
        DECIMAL,

        /**
         * Numeric input.
         */
        NUMERIC,

        /**
         * Telephone input, including asterisk and pound keys.
         */
        TEL,

        /**
         * Display a virtual keyboard optimized for search input.
         */
        SEARCH,

        /**
         * Email input.
         */
        EMAIL,

        /**
         * URL input.
         */
        URL;

        @Override
        public String toString ()
        {
            switch(this)
            {
                case NONE: return "none";
                case TEXT: return "text";
                case DECIMAL: return "decimal";
                case NUMERIC: return "numeric";
                case TEL: return "tel";
                case SEARCH: return "search";
                case EMAIL: return "email";
                case URL: return "url";
            }

            return "";
        }
    }

    public static Attribute href (String href)
    {
        return new Attribute("href", href);
    }

    public static Attribute classes (String... classes)
    {
        return new Attribute("class", Arrays.stream(classes).collect(Collectors.joining(" ")));
    }

    public static Attribute id (String id)
    {
        return new Attribute("id", id);
    }

    public static Attribute src (String src)
    {
        return new Attribute("src", src);
    }

    public static Attribute width (String width)
    {
        return new Attribute("width", width);
    }

    public static Attribute height (String height)
    {
        return new Attribute("height", height);
    }

    public static Attribute crossorigin (String policy)
    {
        return new Attribute("crossorigin", policy);
    }

    public static Attribute crossorigin (CrossoriginPolicy policy)
    {
        return crossorigin(policy.toString());
    }

    /**
     * This attribute names a relationship of the linked document to the current document. The attribute must be a
     * space-separated list of the link types values. The most common use of this attribute is to specify a link to
     * an external style sheet: the rel attribute is set to stylesheet, and the href attribute is set to the URL of
     * an external style sheet to format the page. WebTV also supports the use of the value next for rel to preload
     * the next page in a document series.
     *
     * @return
     */
    public Attribute rel (String... types)
    {
        return new Attribute("rel", Arrays.stream(types).collect(Collectors.joining(" ")));
    }

    /**
     * This attribute is used to define the type of the content linked to. The value of the attribute should be a MIME
     * type such as text/html, text/css, and so on. The common use of this attribute is to define the type of style
     * sheet linked and the most common current value is text/css, which indicates a Cascading Style Sheet format.
     *
     * @param type
     * @return
     */
    public static Attribute type (String type)
    {
        return new Attribute("type", type);
    }

    public static Attribute type (MediaType mimeType)
    {
        return type(mimeType.toString());
    }

    /**
     * A string indicating which referrer policy to use when fetching the resource
     *
     * @param policy
     * @return
     */
    public static Attribute referrerPolicy (String policy)
    {
        return new Attribute("referrerpolicy", policy);
    }

    public static Attribute referrerPolicy (ReferrerPolicy policy)
    {
        return referrerPolicy(policy.toString());
    }

    /**
     * This attribute specifies the media which the linked resource applies to. Its value must be a media query. This
     * attribute is mainly useful when linking to external stylesheets by allowing the user agent to pick the best
     * adapted one for the device it runs on.
     *
     * @param mq
     * @return
     */
    public static Attribute media (String mq)
    {
        return new Attribute("media", mq);
    }

    /**
     * Contains inline metadata, a base64-encoded cryptographic hash of a resource (file) you’re telling the browser to
     * fetch, that a user agent can use to verify that a fetched resource has been delivered free of unexpected
     * manipulation.
     *
     * @param hash
     * @return
     */
    public static Attribute integrity (String hash)
    {
        return new Attribute("integrity", hash);
    }

    public static Attribute accept (String types)
    {
        return new Attribute("accept", types);
    }

    public static Attribute accept (MediaType... mimeTypes)
    {
        return accept(
            Arrays.stream(mimeTypes)
                .map(mt -> mt.toString())
                .collect(Collectors.joining(","))
        );
    }

    public static Attribute autocomplete (String autocomplete)
    {
        return new Attribute("autocomplete", autocomplete);
    }

    public static Attribute autocomplete (boolean active)
    {
        return autocomplete(active ? "on" : "off");
    }

    public static Attribute autofocus ()
    {
        return new Attribute("autofocus", "on");
    }

    public static Attribute checked ()
    {
        return new Attribute("checked", "checked");
    }

    public static Attribute disabled ()
    {
        return new Attribute("disabled", "disabled");
    }

    public static Attribute inputmode (String mode)
    {
        return new Attribute("inputmode", mode);
    }

    public static Attribute inputmode (InputMode mode)
    {
        return inputmode(mode.toString());
    }

    public static Attribute name (String name)
    {
        return new Attribute("name", name);
    }

    public static Attribute pattern (String pattern)
    {
        return new Attribute("pattern", pattern);
    }

    public static Attribute placeholder (String placeholder)
    {
        return new Attribute("placeholder", placeholder);
    }
}
