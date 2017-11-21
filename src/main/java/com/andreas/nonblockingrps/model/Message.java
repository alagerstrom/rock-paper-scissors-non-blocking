package com.andreas.nonblockingrps.model;

import java.io.Serializable;

public class Message implements Serializable{
    private MessageType type;
    private String content;
    private Player sender;
    private PlayCommand playCommand = PlayCommand.ROCK;
    private GameRoundDTO gameRound;

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public Message setType(MessageType type) {
        this.type = type;
        return this;
    }
    public Message setGameRound(GameRoundDTO gameRound){
        this.gameRound = gameRound;
        return this;
    }

    public GameRoundDTO getGameRound() {
        return gameRound;
    }

    public String getContent() {
        return content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    public Player getSender() {
        return sender;
    }

    public Message setSender(Player sender) {
        this.sender = sender;
        return this;
    }

    public PlayCommand getPlayCommand() {
        return playCommand;
    }

    public Message setPlayCommand(PlayCommand playCommand) {
        this.playCommand = playCommand;
        return this;
    }
}
