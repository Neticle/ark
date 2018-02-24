package pt.neticle.ark.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import pt.neticle.ark.base.Application;
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
