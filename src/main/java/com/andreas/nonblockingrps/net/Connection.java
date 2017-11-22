package com.andreas.nonblockingrps.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;

public class Connection<T> extends ChannelInboundHandlerAdapter{

    private final NetHandler<T> netHandler;
    private final Channel channel;


    Connection(Channel channel, NetHandler netHandler) throws IOException {
        this.channel = channel;
        this.netHandler = netHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NetMessage message = (NetMessage) msg;
        netHandler.handleIncomingMessage(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    public synchronized void send(NetMessage netMessage) throws IOException {
        channel.writeAndFlush(netMessage);
    }
}
