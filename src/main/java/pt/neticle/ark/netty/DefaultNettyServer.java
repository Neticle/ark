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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import pt.neticle.ark.base.WebApplication;

/**
 * A very crude implementation of a HTTP server based on Netty.
 *
 * It's basically being used as a tool for testing during development, it needs to be revisioned.
 */
public class DefaultNettyServer
{
    private final Integer inboundPort;

    public DefaultNettyServer (Integer port)
    {
        this.inboundPort = port;
    }

    public void serve (WebApplication application)
    {
        SslContext sslContext = null;
        SelfSignedCertificate ssc = null;

        /*try
        {
            ssc = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } catch (CertificateException e)
        {
            e.printStackTrace();
        } catch (SSLException e)
        {
            e.printStackTrace();
        }*/


        EventLoopGroup masterGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();

        b.group(masterGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new Initializer(sslContext, application));

        this.onBeforeListen(b);

        Channel ch;

        System.out.println("Start listening on port " + this.inboundPort);

        try
        {
            ch = b.bind(this.inboundPort).sync().channel();

            System.out.println("Listening...");

            ch.closeFuture().sync();

            System.out.println("Closed.");
        } catch(InterruptedException e)
        {
            System.out.println("Service interrupted... Shutting down.");
        } finally
        {
            workerGroup.shutdownGracefully();
            masterGroup.shutdownGracefully();
        }
    }

    protected void onBeforeListen (ServerBootstrap bootstrap)
    {
    }

    private static class Initializer extends ChannelInitializer<SocketChannel>
    {
        private final SslContext sslCtx;
        private final WebApplication application;

        public Initializer (SslContext sslCtx, WebApplication application)
        {
            this.sslCtx = sslCtx;
            this.application = application;
        }

        protected void initChannel (SocketChannel socketChannel) throws Exception
        {
            ChannelPipeline p = socketChannel.pipeline();

            if(this.sslCtx != null)
            {
                p.addLast(sslCtx.newHandler(socketChannel.alloc()));
            }

            p.addLast(new HttpRequestDecoder());

            // Uncomment the following line if you don't want to handle HttpChunks.
            p.addLast(new HttpObjectAggregator(1048576));

            p.addLast(new HttpResponseEncoder());

            // Remove the following line if you don't want automatic content compression.
            //p.addLast(new HttpContentCompressor());

            p.addLast(new NettyAdapter(this.application));
        }
    }
}
