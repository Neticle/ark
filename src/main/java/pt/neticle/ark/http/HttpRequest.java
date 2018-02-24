package pt.neticle.ark.http;

import java.net.HttpCookie;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Stream;

public interface HttpRequest extends HttpMessage
{
    enum Method
    {
        GET,
        HEAD,
        POST,
        PUT,
        DELETE,
        CONNECT,
        OPTIONS,
        TRACE,
        PATCH
    }

    List<HttpCookie> getCookies ();

    Stream<HttpCookie> cookies ();

    String getHost ();

    Method getMethod ();

    boolean is (Method method);

    String getUri ();

    ByteBuffer getBody ();

    String getPath ();

    String getQueryString ();
}
