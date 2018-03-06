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

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import pt.neticle.ark.base.WebApplication;

public class NettyAdapter extends SimpleChannelInboundHandler<Object>
{
    private final WebApplication application;

    public NettyAdapter (WebApplication application)
    {
        this.application = application;
    }

    @Override
    protected void channelRead0 (ChannelHandlerContext chCtx, Object message) throws Exception
    {
        if(message instanceof HttpRequest)
        {
            if(HttpUtil.is100ContinueExpected((HttpRequest)message))
            {
                chCtx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }
        }

        if(message instanceof FullHttpRequest)
        {
            NettyHttpRequest request;
            NettyHttpResponse response;

            try
            {
                request = new NettyHttpRequest((FullHttpRequest) message);
            } catch (Throwable e)
            {
                // Catch anything during request creation so we know to send a bad request respond and end it here

                // TODO: Send the actual bad-request response

                chCtx.flush();
                chCtx.close();

                e.printStackTrace();

                return;
            }

            response = new NettyHttpResponse();

            application.dispatch(request, response);

            if(!sendResponse(chCtx, response))
            {
                // if we're not keeping alive, close.
                chCtx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private boolean sendResponse (ChannelHandlerContext ctx, FullHttpResponse response)
    {
        boolean keepAlive = false;// HttpUtil.isKeepAlive(this.request);

        if(keepAlive)
        {
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

            // keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ctx.write(response);

        return keepAlive;
    }
}
