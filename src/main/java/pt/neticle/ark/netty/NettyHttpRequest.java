// Copyright 2018 Igor Azevedo <igor.azevedo@neticle.pt>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package pt.neticle.ark.netty;

import io.netty.handler.codec.http.*;
import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.http.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpCookie;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class NettyHttpRequest implements HttpRequest
{
    private final FullHttpRequest underlyingObject;
    private final ContentType contentType;
    private final List<HttpCookie> cookies;
    private final Method method;
    private final String host;
    private final String path;
    private final String queryString;

    public NettyHttpRequest (FullHttpRequest nettyRequest) throws ParseException
    {
        underlyingObject = nettyRequest;

        contentType = underlyingObject.headers().contains("Content-Type") ?
            ContentType.parse(underlyingObject.headers().get("Content-Type")) : null;

        cookies = underlyingObject.headers().contains("Cookies") ?
            HttpCookie.parse(underlyingObject.headers().get("Cookies")) : new ArrayList<>();

        method = Method.valueOf(underlyingObject.method().name());

        host = underlyingObject.headers().get("Host");

        String uri = underlyingObject.uri();
        int qsMarker = uri.indexOf('?');

        if(qsMarker == -1)
        {
            path = uri;
            queryString = "";
        }
        else
        {
            path = uri.substring(0, qsMarker);
            queryString = uri.substring(qsMarker+1);
        }
    }


    @Override
    public List<HttpCookie> getCookies ()
    {
        return cookies;
    }

    @Override
    public Stream<HttpCookie> cookies ()
    {
        return cookies.stream();
    }

    @Override
    public String getHost ()
    {
        return host;
    }

    @Override
    public Method getMethod ()
    {
        return method;
    }

    @Override
    public boolean is (Method method)
    {
        return this.method == method;
    }

    @Override
    public String getUri ()
    {
        return underlyingObject.uri();
    }

    @Override
    public InputStream getBody ()
    {
        return new ByteArrayInputStream(underlyingObject.content().nioBuffer().array());
    }

    @Override
    public String getPath ()
    {
        return path;
    }

    @Override
    public String getQueryString ()
    {
        return queryString;
    }

    @Override
    public String getHeader (String header)
    {
        return underlyingObject.headers().get(header);
    }

    @Override
    public ContentType getContentType ()
    {
        return contentType;
    }
}
