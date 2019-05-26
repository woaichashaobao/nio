package com.bj58.risk.netty.messagepack;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.List;

public class EchoClient {

    public static void main(String[] args) {
        EchoClient echoClient = new EchoClient();
        echoClient.connect("127.0.0.1", 8080);
    }


    public void connect(String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("msg encoder", new MessageEncoder())
                                    .addLast("msg decoder", new MessageDecoder())
                                    .addLast(new EchoClientHandler())
                            ;
                        }
                    });
            ChannelFuture sync = bootstrap.connect(host, port).sync();
            sync.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    private class EchoClientHandler extends ChannelHandlerAdapter {

        private int counter;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            List<UserInfo> list = new ArrayList<>();
            int count = 1000;
            while (count > 0) {
                count--;
                UserInfo wangwenchang = new UserInfo("wangwenchang", 29);
                list.add(wangwenchang);
            }
            list.forEach(userInfo -> ctx.write(userInfo));
            ctx.flush();
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            counter++;
            System.out.println(msg);
            ctx.write(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
