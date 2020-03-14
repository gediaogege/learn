package com.example.learn.io.netty;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;


/**
 * @author qinmintao
 * @description:
 * @date 2020-03-14 11:01
 */
public class NettyServerHandel implements ChannelInboundHandler {
    //保存连接到服务端的channel集合
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int readIdleTimes = 0;

    @Override
    public void channelRegistered(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        //有连接事件会触发,当有新的连接来，给所有的已连接客户端发送消息
        channelGroup.writeAndFlush("[客户端：]" + channelHandlerContext.channel().remoteAddress() + "上线了" + "\n");
        channelGroup.add(channelHandlerContext.channel());
        System.out.println(channelHandlerContext.channel().remoteAddress() + "上线了");
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        //断开连接触发
        System.out.println(channelHandlerContext.channel().remoteAddress() + "下线了");
        channelGroup.remove(channelHandlerContext.channel());
        channelGroup.writeAndFlush("[客户端：]" + channelHandlerContext.channel().remoteAddress() + "下线了");
        System.out.println("当前在线人数" + channelGroup.size());
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        //服务端收到客户端发过来的消息触发
        Channel channel = channelHandlerContext.channel();
        if ("I am alive".equals(o)) {
            channel.writeAndFlush("ok");
        } else {
            channelGroup.forEach(ch -> {
                if (ch != channel) {
                    ch.writeAndFlush("[客户端：]" + ch.remoteAddress() + "发送了消息：" + (String) o);
                } else {
                    ch.writeAndFlush("自己发送了消息: " + (String) o);
                }
            });
            System.out.println(channel.remoteAddress() + ":" + o);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
        //给处理完客户端发过来的信息后得回调函数
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        IdleStateEvent event = (IdleStateEvent) o;

        String eventType = null;
        switch (event.state()) {
            case READER_IDLE:
                eventType = "读空闲";
                readIdleTimes++; // 读空闲的计数加1
                break;
            case WRITER_IDLE:
                eventType = "写空闲";
                // 不处理
                break;
            case ALL_IDLE:
                eventType = "读写空闲";
                // 不处理
                break;
        }
        System.out.println(channelHandlerContext.channel().remoteAddress() + "超时事件：" + eventType);
        if (readIdleTimes > 3) {
            System.out.println(" [server]读空闲超过3次，关闭连接");
            channelHandlerContext.channel().writeAndFlush("you are out");
            channelHandlerContext.channel().close();
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
        channelHandlerContext.close();
    }
}
