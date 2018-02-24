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
