package com.example.learn.io.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author qinmintao
 * @description:
 * @date 2020-03-13 15:22
 */
public class NIOClient {
    public static void main(String[] args) throws Exception {
        //获取客户端channel
        SocketChannel channel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8099));
        channel.configureBlocking(false);
        //获取selector
        Selector selector = Selector.open();
        //客户端的channel注册到selector上，监听读事件
        channel.register(selector, SelectionKey.OP_READ);
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                try {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        while (clientChannel.read(buffer) > 0) {
                            buffer.flip();
                            System.out.println("接收到服务端的消息: " + new String(buffer.array()));
                            buffer.clear();
                        }
                    }
                } finally {
                   iterator.remove();
                }
            }
        }
    }
}
