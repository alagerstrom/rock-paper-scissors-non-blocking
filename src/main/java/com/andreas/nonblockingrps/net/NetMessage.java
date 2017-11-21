package com.andreas.nonblockingrps.net;

import java.io.Serializable;

public class NetMessage<T> implements Serializable{
    private NetMessageType type;
    private int number;
    private Peer sender;
    private T content;

    NetMessage(NetMessageType type) {
        this.type = type;
    }

    NetMessageType getType() {
        return type;
    }

    public NetMessage setType(NetMessageType type) {
        this.type = type;
        return this;
    }

    T getContent() {
        return content;
    }

    NetMessage setContent(T content) {
        this.content = content;
        return this;
    }

    int getNumber() {
        return number;
    }

    NetMessage setNumber(int number) {
        this.number = number;
        return this;
    }

    Peer getSender() {
        return sender;
    }

    NetMessage setSender(Peer sender) {
        this.sender = sender;
        return this;
    }

}
