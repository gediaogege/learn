package com.example.learn.io.nio;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author qinmintao
 * @description:
 * @date 2020-03-13 14:28
 */
public class NIOServer {
    public static void main(String[] args) throws Exception {
        //获取server端的channel
        ServerSocketChannel channel = ServerSocketChannel.open();
        //设置为非阻塞
        channel.configureBlocking(false);
        //监听8099端口
        channel.bind(new InetSocketAddress(8099));
        //获取选择器selector
        Selector selector = Selector.open();
        //selector监听server端channel的连接事件(有客户端来连接服务端就会触发这个事件)
        channel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            //轮询监听注册到selector上的事件
            selector.select();
            //获取注册到selector上的所有的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                //selectionKey 保存着服务端和客户端建立连接的channel
                SelectionKey selectionKey = iterator.next();
                try {
                    if (selectionKey.isAcceptable()) {
                        System.out.println("有客户端连接了。。。");
                        SocketChannel clientChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
                        clientChannel.configureBlocking(false);
                        //监听读事件，同时注册到selector上
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        //给客户端发送一条消息
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        buffer.put("hello client".getBytes());
                        clientChannel.write(buffer);
                        buffer.clear();
                    } else if (selectionKey.isReadable()) {
                        //客户端给服务端发消息了，这时就是读事件
                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer allocate = ByteBuffer.allocate(1024);
                        while (clientChannel.read(allocate) > 0) {
                            allocate.flip();
                            System.out.println("接受到客户端的消息：" + new String(allocate.array()));
                            allocate.clear();
                        }
                    }
                    iterator.remove();
                } finally {
                   // iterator.remove();
                }
            }

        }
    }


}
