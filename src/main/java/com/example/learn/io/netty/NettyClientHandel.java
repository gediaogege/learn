package com.example.learn.io.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author qinmintao
 * @description:
 * @date 2020-03-14 11:23
 */
public class NettyClientHandel extends SimpleChannelInboundHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        //接收到读事件触发
        System.out.println(o);
    }
}
