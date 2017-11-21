package com.andreas.nonblockingrps.model;

import java.util.List;

public interface GameObserver {
    void allPlayers(List<String> allPlayers);
    void playerJoinedTheGame(String player);
    void playerLeftTheGame(String player);
    void chatMessage(String message);
    void draw();
    void victory(int roundScore, int totalScore);
    void loss();
    void newRound(int totalScore);
}
