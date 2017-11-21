package com.andreas.nonblockingrps.controller;

import com.andreas.nonblockingrps.model.*;
import com.andreas.nonblockingrps.util.Constants;
import com.andreas.nonblockingrps.util.Logger;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AppController implements GameObserver {
    private final static AppController instance = new AppController();

    private Game game;
    private NetDelegate netDelegate;

    private AppController() {
        Logger.log("AppController created");
    }

    public static AppController getInstance() {
        return instance;
    }

    public void createNewGame(String playerName, int port, CompletionHandler<Void, Void> completionHandler) {
        CompletableFuture.runAsync(() -> {

            try {
                netDelegate = new NetDelegate(port);
            } catch (IOException e) {
                completionHandler.failed(e, null);
            }

            String uniqueName = netDelegate.getUniqueName();
            game = new Game(playerName, uniqueName);
            game.addGameObserver(this);
            try {
                Thread.sleep(Constants.FAKE_NETWORK_DELAY);
            } catch (InterruptedException ignored) {
            }
            netDelegate.addNetObserver(game);
            completionHandler.completed(null, null);
        });
    }


    public void connectTo(String host, int port, CompletionHandler<Void, Void> completionHandler) {
        CompletableFuture.runAsync(() -> {
            netDelegate.getLocalHost();
            try {
                netDelegate.connectTo(host, port);
                sendPlayerInfo(new CompletionHandler<Void, Void>() {
                    @Override
                    public void completed(Void result, Void attachment) {
                        completionHandler.completed(null, null);
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        completionHandler.failed(exc, null);
                    }
                });
            } catch (IOException e) {
                completionHandler.failed(e, null);
            }
        });
    }

    public void getLocalHost(CompletionHandler<String, Void> completionHandler) {
        CompletableFuture.runAsync(() -> {
            String localHost = netDelegate.getLocalHost();
            String tokens[] = localHost.split(Constants.IP_DELIMITER);
            if (tokens.length < 1)
                completionHandler.failed(new Exception(), null);
            completionHandler.completed(tokens[tokens.length - 1], null);
        });
    }

    public void getLocalPort(CompletionHandler<Integer, Void> completionHandler) {
        CompletableFuture.runAsync(() -> {
            int port = netDelegate.getLocalPort();
            completionHandler.completed(port, null);
        });
    }

    public void sendRoundInfo(){
        CompletableFuture.runAsync(()->{
           Message message = new Message(MessageType.ROUND_INFO).setGameRound(game.getGameRound());
            try {
                netDelegate.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    public void sendPlayerInfo(CompletionHandler<Void, Void> nullableCompletionHandler) {
        CompletableFuture.runAsync(() -> {
            String playerName = game.getUniqueName();
            Message message = new Message(MessageType.PLAYER_INFO)
                    .setContent(playerName);
            try {
                netDelegate.sendMessage(message.setSender(game.getPlayer()));
                if (nullableCompletionHandler != null)
                    nullableCompletionHandler.completed(null, null);
            } catch (IOException e) {
                if (nullableCompletionHandler != null)
                    nullableCompletionHandler.failed(e, null);
            }
        });
    }

    public void sendChatMessage(String messageContent, CompletionHandler<Void, Void> completionHandler) {
        Message message = new Message(MessageType.CHAT).setContent(messageContent);
        try {
            netDelegate.sendMessage(message.setSender(game.getPlayer()));
            completionHandler.completed(null, null);
        } catch (IOException e) {
            completionHandler.failed(e, null);
        }
    }

    public void sendPlayRock(CompletionHandler<Void, Void> completionHandler) {
        sendPlay(PlayCommand.ROCK, completionHandler);
    }

    public void sendPlayPaper(CompletionHandler<Void, Void> completionHandler) {
        sendPlay(PlayCommand.PAPER, completionHandler);
    }

    public void sendPlayScissors(CompletionHandler<Void, Void> completionHandler) {
        sendPlay(PlayCommand.SCISSORS, completionHandler);
    }

    private void sendPlay(PlayCommand playCommand, CompletionHandler<Void, Void> completionHandler) {
        Message message = new Message(MessageType.PLAY)
                .setSender(game.getPlayer())
                .setPlayCommand(playCommand);
        try {
            netDelegate.sendMessage(message.setSender(game.getPlayer()));
            completionHandler.completed(null, null);
        } catch (IOException e) {
            completionHandler.failed(e, null);
        }
    }

    public void addGameObserver(GameObserver gameObserver) {
        CompletableFuture.runAsync(() -> {
            game.addGameObserver(gameObserver);
        });
    }

    public void getPlayerName(CompletionHandler<String, Void> completionHandler) {
        CompletableFuture.runAsync(() -> {
            String result = game.getDisplayName();
            completionHandler.completed(result, null);
        });
    }

    @Override
    public void allPlayers(List<String> allPlayers) {

    }

    @Override
    public void playerJoinedTheGame(String player) {
        sendPlayerInfo(null);
        if (game.getGameRound() != null)
            sendRoundInfo();
    }

    @Override
    public void playerLeftTheGame(String player) {

    }

    @Override
    public void chatMessage(String message) {

    }

    @Override
    public void draw() {

    }

    @Override
    public void victory(int roundScore, int totalScore) {

    }

    @Override
    public void loss() {

    }

    @Override
    public void newRound(int totalScore) {

    }
}
