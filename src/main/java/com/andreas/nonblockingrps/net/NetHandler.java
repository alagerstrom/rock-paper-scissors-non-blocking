package com.andreas.nonblockingrps.net;

import com.andreas.nonblockingrps.util.Constants;
import com.andreas.nonblockingrps.util.Logger;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.CompletionHandler;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NetHandler<T> {
    private final List<Connection> connections = new ArrayList<>();
    private int sendMessageCounter;
    private final ArrayList<Peer> knownPeers = new ArrayList<>();
    private final ConcurrentHashMap<Peer, Long> lastTimeHeardFromPeer = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Peer, Integer> seenMessages = new ConcurrentHashMap<>();
    private final Peer peer;
    private final String ip;
    private final String uniqueName;
    private int localPort;

    public NetHandler(int port, Delegate<T> delegate) {
        ip = createIpAddress();
        this.delegate = delegate;
        uniqueName = createUniqueName(port);
        this.peer = new Peer(uniqueName);
        localPort = port;
        startSendingHeartbeats();
    }

    private String createIpAddress() {
        String ipAddress;
        try {
            ipAddress = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            int randomNumber = (int) (Math.random() * 1000000);
            ipAddress = "[No ip " + randomNumber + "]";
        }
        return ipAddress;
    }

    private String createUniqueName(int port) {
        return ip + " " + port + " ";
    }

    private Delegate<T> delegate;

    public interface Delegate<T> {
        void onNewMessage(T message);

        void peerNotResponding(String uniqueName);
    }

    private class HeartbeatSender extends TimerTask {
        @Override
        public void run() {
            try {
                sendHeartbeat();
            } catch (IOException e) {
                Logger.log("Failed to send heartbeat");
            }
        }
    }

    private class HeartbeatCounter extends TimerTask {
        @Override
        public void run() {
            checkIfHeartbeatsHaveBeenReceived();
        }

    }

    private void checkIfHeartbeatsHaveBeenReceived() {
        synchronized (knownPeers) {
            for (int i = 0; i < knownPeers.size(); i++) {
                Peer peer = knownPeers.get(i);
                if (peer == null)
                    return;
                long now = System.currentTimeMillis();
                long lastHeartbeat = lastTimeHeardFromPeer.get(peer);
                if (now - lastHeartbeat > Constants.HEARTBEAT_TIMEOUT_MS) {
                    if (delegate != null)
                        delegate.peerNotResponding(peer.getName());
                    seenMessages.remove(peer);
                    lastTimeHeardFromPeer.remove(peer);
                    knownPeers.remove(peer);
                    i--;
                }
            }
        }

    }

    private void startSendingHeartbeats() {
        Timer timer = new Timer(true);
        timer.schedule(new HeartbeatSender(), 0, 1000);
        timer.schedule(new HeartbeatCounter(), 0, 500);
    }

    private void sendHeartbeat() throws IOException {
        NetMessage<T> netMessage = new NetMessage<>(NetMessageType.HEARTBEAT);
        sendNetMessage(netMessage);
    }

    public String getLocalHost() {
        return ip;
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    void removeConnection(Connection connection) {
        synchronized (connections) {
            connections.remove(connection);
        }
    }

    synchronized Connection addConnection(SocketChannel socketChannel) throws IOException {
        Connection connection = new Connection(socketChannel, NetHandler.this);
        synchronized (connections) {
            connections.add(connection);
        }
        return connection;
    }

    public void connectTo(String host, int port) throws IOException {
        Logger.log("Connecting to " + host + " " + port);

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Constants.CONNECT_TIMEOUT_MS);

            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(
                            new ObjectEncoder(),
                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                            addConnection(socketChannel));
                }
            });
            clientBootstrap.connect(host, port).sync();

        } catch (InterruptedException e) {
            throw new IOException();
        }
    }

    private void broadcast(NetMessage<T> netMessage) throws IOException {
        handleIncomingMessage(netMessage);
        synchronized (connections) {
            for (Connection connection : connections)
                connection.send(netMessage);
        }
    }

    synchronized void handleIncomingMessage(NetMessage<T> netMessage) throws IOException {
        Logger.log("Handling incoming message " + netMessage);
        if (!isNewMessage(netMessage))
            return;
        broadcast(netMessage);
        switch (netMessage.getType()) {
            case MESSAGE:
                if (delegate != null) {
                    delegate.onNewMessage(netMessage.getContent());
                }
                break;
            case HEARTBEAT:
                long now = System.currentTimeMillis();
                lastTimeHeardFromPeer.put(netMessage.getSender(), now);
                if (!knownPeers.contains(netMessage.getSender()))
                    knownPeers.add(netMessage.getSender());
                break;
            default:
                Logger.log("Received unknown message");
                break;
        }
    }

    private boolean isNewMessage(NetMessage netMessage) {
        boolean isNew = false;
        if (seenMessages.containsKey(netMessage.getSender())) {
            int oldNumber = seenMessages.get(netMessage.getSender());
            if (oldNumber < netMessage.getNumber())
                isNew = true;
        } else {
            isNew = true;
        }
        if (isNew)
            seenMessages.put(netMessage.getSender(), netMessage.getNumber());
        return isNew;
    }

    public void sendMessage(T message) throws IOException {
        NetMessage<T> netMessage = new NetMessage<>(NetMessageType.MESSAGE);
        netMessage.setContent(message);
        sendNetMessage(netMessage);
    }

    private void sendNetMessage(NetMessage<T> netMessage) throws IOException {
        synchronized (this) {
            netMessage.setNumber(sendMessageCounter++);
        }
        netMessage.setSender(peer);
        broadcast(netMessage);
    }

    public void startAcceptingIncomingConnections(CompletionHandler<Void, Void> completionHandler) {
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoopGroup group2 = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(group, group2)
                    .channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(
                            new ObjectEncoder(),
                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                            addConnection(socketChannel));
                }
            });
            ChannelFuture channelFuture = serverBootstrap.localAddress(localPort).bind().sync();
            completionHandler.completed(null, null);
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            completionHandler.failed(e, null);
        }
    }
}
