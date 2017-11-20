package main.java.com.andreas.rockpaperscissors.model;


public interface NetObserver {
    void playerInfo(Player player);
    void playerNotResponding(String uniqueName);
    void playerPlaysCommand(Player player, PlayCommand playCommand);
    void chatMessage(String message);
    void roundInfo(GameRoundDTO gameRoundDTO);
}
