package com.bj58.risk.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MultiplexerTimeServer implements Runnable {

    private Selector selector;

    private ServerSocketChannel server;

    private int port;

    private boolean isStop;

    public MultiplexerTimeServer(int port) {
        this.port = port;
        this.isStop = false;
        try {
            this.selector = Selector.open();
            this.server = ServerSocketChannel.open();
            this.server.socket().bind(new InetSocketAddress(8080));
            this.server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("the time server start in port" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!isStop) {
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                SelectionKey key = null;
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    key = iterator.next();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws Exception {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                ServerSocketChannel server = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = server.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);
                System.out.println("time server accept a scoket");
            }
            if (key.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(byteBuffer);
                if (readBytes > 0) {
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    String body = new String(bytes);
                    System.out.println("the time server receiver order : " + body);
                    String currentTime = "query time order".equals(body) ? new Date().toString() : "bad order";
                    doWrite(currentTime, socketChannel);

                } else if (readBytes < 0) {
                    System.out.println("close the key and close the channel");
                    key.cancel();
                    socketChannel.close();
                }
            }
        }
    }

    private void doWrite(String currentTime, SocketChannel socketChannel) {
        if (currentTime != null) {
            byte[] bytes = currentTime.getBytes();
            ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.flip();
            try {
                socketChannel.write(byteBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static ExecutorService executor = null;

    public static void main(String[] args) {
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new MultiplexerTimeServer(8080));
    }
}
