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

import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import pt.neticle.ark.data.ContentType;
import pt.neticle.ark.http.HttpResponse;

import java.io.OutputStream;

public class NettyHttpResponse extends DefaultFullHttpResponse implements HttpResponse
{
    private final ByteBufOutputStream os;

    public NettyHttpResponse ()
    {
        super(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

        this.os = new ByteBufOutputStream(this.content());
    }

    @Override
    public OutputStream contentOutput ()
    {
        return this.os;
    }

    @Override
    public int getStatusCode ()
    {
        return 0;
    }

    @Override
    public void setStatusCode (int statusCode)
    {
        this.setStatus(HttpResponseStatus.valueOf(statusCode));
    }

    @Override
    public String getHeader (String header)
    {
        return this.headers().get(header);
    }

    @Override
    public ContentType getContentType ()
    {
        return null;
    }

    @Override
    public void setHeader (String header, String value)
    {
        this.headers().set(header, value);
    }
}
