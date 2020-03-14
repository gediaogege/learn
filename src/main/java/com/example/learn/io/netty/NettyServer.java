package com.example.learn.io.netty;

import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author qinmintao
 * @description:
 * @date 2020-03-14 10:34
 */
public class NettyServer {
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //添加channel时，有出栈和入栈的的区别；以客户端为例，当给客户端发送一条数据。这时对于客户端就是入栈。
                            //这时的pipeline的顺序是从head到tail ,而且是只执行inBandHandel。就是decoder-->NettyServerHandel(解码-->接收)
                            //如果是客户端发送一条时间给服务端，这时对于客户端是出栈操作，pipeline的执行顺序是从tail到head
                            //而且只执行outBandHandel.就是NettyServerHandel-->encoder(发送-->编码);
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("decoder",new StringDecoder());
                            pipeline.addLast("encoder",new StringEncoder());
                            pipeline.addLast(new NettyServerHandel());
                        }
                    });
            System.out.println("netty server start");
            //sync()表示同步等待绑定端口执行完才执行后面的操作
            ChannelFuture cf = bootstrap.bind(8088).sync();
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
