package com.example.learn.io.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @author qinmintao
 * @description:
 * @date 2020-03-14 10:47
 */
public class NettyClient {
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            ChannelInitializer ch = new ChannelInitializer() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast("decoder", new StringDecoder());//解码
                    pipeline.addLast("encoder", new StringEncoder());//编
                    pipeline.addLast(new NettyClientHandel());
                }
            };
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(ch);
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8088).sync();
            System.out.println("======="+channelFuture.channel().remoteAddress()+"======");
            Scanner sc = new Scanner(System.in);
            while (sc.hasNext()){
                channelFuture.channel().writeAndFlush(sc.next());
            }
            //channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
