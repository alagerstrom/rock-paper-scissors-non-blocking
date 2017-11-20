package main.java.com.andreas.rockpaperscissors.model;

import com.andreas.rockpaperscissors.util.Logger;

import java.util.*;

class GameRound {

    private List<Player> players = new ArrayList<>();
    private Map<Player, PlayCommand> playCommandMap = new HashMap<>();
    private Game game;

    GameRound(List<Player> playerList, Game game) {
        this.players = playerList;
        this.game = game;
    }

    synchronized void playerPlaysCommand(Player player, PlayCommand playCommand) {
        playCommandMap.put(player, playCommand);
        Logger.log("Checking if round is complete...");
        tellGameIfRoundCompleted();
    }

    private void tellGameIfRoundCompleted() {
        if (isRoundComplete())
            game.roundCompleted(this);
    }

    private boolean isRoundComplete() {
        boolean result = true;
        for (Player player : players) {
            if (!playCommandMap.containsKey(player)) {
                result = false;
                break;
            }
        }
        return result;
    }

    synchronized void removePlayer(Player player) {
        players.remove(player);
        tellGameIfRoundCompleted();
    }

    private PlayCommand getPlayCommand(Player player) {
        return playCommandMap.get(player);
    }

    synchronized boolean isDraw() {
        return playCommandMap.containsValue(PlayCommand.ROCK) &&
                playCommandMap.containsValue(PlayCommand.SCISSORS) &&
                playCommandMap.containsValue(PlayCommand.PAPER)
                || containsSingleValue(playCommandMap);
    }

    private boolean containsSingleValue(Map<Player, PlayCommand> playCommandMap) {
        List<PlayCommand> commands = new ArrayList<>();
        commands.addAll(playCommandMap.values());
        if (commands.size() < 2)
            return true;
        return count(commands.get(0)) == commands.size();
    }

    private int count(PlayCommand playCommand) {
        List<PlayCommand> commands = new ArrayList<>();
        commands.addAll(playCommandMap.values());
        return Collections.frequency(commands, playCommand);
    }

    synchronized int scoreForWinner(Player winner) {
        PlayCommand playCommand = getPlayCommand(winner);
        switch (playCommand) {
            case ROCK:
                return count(PlayCommand.SCISSORS);
            case PAPER:
                return count(PlayCommand.ROCK);
            case SCISSORS:
                return count(PlayCommand.PAPER);
        }
        return 0;
    }

    synchronized boolean isWonBy(Player player) {
        PlayCommand playCommand = playCommandMap.get(player);
        switch (playCommand) {
            case ROCK:
                if (count(PlayCommand.PAPER) == 0)
                    return !isDraw();
                break;
            case PAPER:
                if (count(PlayCommand.SCISSORS) == 0)
                    return !isDraw();
                break;
            case SCISSORS:
                if (count(PlayCommand.ROCK) == 0)
                    return !isDraw();
        }
        return false;
    }

    public void addPlayer(Player player) {
        if (!players.contains(player))
            players.add(player);
    }

    public GameRoundDTO getDTO(){
        return new GameRoundDTO(players, playCommandMap);
    }
}
