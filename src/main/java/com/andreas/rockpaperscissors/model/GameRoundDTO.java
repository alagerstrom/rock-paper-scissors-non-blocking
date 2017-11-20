package main.java.com.andreas.rockpaperscissors.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GameRoundDTO implements Serializable {

    private final List<Player> players;
    private final Map<Player, PlayCommand> playCommandMap;

    public GameRoundDTO(List<Player> players, Map<Player, PlayCommand> playCommandMap) {
        this.players = players;
        this.playCommandMap = playCommandMap;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Map<Player, PlayCommand> getPlayCommandMap() {
        return playCommandMap;
    }
}
