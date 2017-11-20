package main.java.com.andreas.rockpaperscissors.model;

import com.andreas.rockpaperscissors.net.NetHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NetDelegate implements NetHandler.Delegate<Message> {

    private List<NetObserver> netObservers = new ArrayList<>();

    private NetHandler<Message> netHandler;
    private String uniqueName;

    public NetDelegate(int port) throws IOException {
        netHandler = new NetHandler<>(port, this);
        uniqueName = netHandler.getUniqueName();
    }

    public String getUniqueName(){
        return uniqueName;
    }
    @Override
    public void onNewMessage(Message message) {
        switch (message.getType()) {
            case CHAT:
                notifyChatObservers("[" + message.getSender().getDisplayName() + "] " + message.getContent());
                break;
            case PLAYER_INFO:
                notifyPlayerObservers(message.getSender());
                break;
            case PLAY:
                notifyPlayerPlaysCommand(message.getSender(), message.getPlayCommand());
                break;
            case ROUND_INFO:
                notifyRoundInfo(message.getGameRound());
                break;
        }
    }

    private void notifyRoundInfo(GameRoundDTO gameRound) {
        for (NetObserver netObserver : netObservers)
            netObserver.roundInfo(gameRound);
    }

    @Override
    public void peerNotResponding(String uniqueName) {
        for (NetObserver netObserver : netObservers)
            netObserver.playerNotResponding(uniqueName);

    }

    private void notifyPlayerObservers(Player player) {
        for (NetObserver netObserver : netObservers) {
            netObserver.playerInfo(player);
        }
    }

    private void notifyChatObservers(String messageContent) {
        for (NetObserver netObserver : netObservers)
            netObserver.chatMessage(messageContent);
    }


    public void addNetObserver(NetObserver netObserver) {
        this.netObservers.add(netObserver);
    }

    private void notifyPlayerPlaysCommand(Player player, PlayCommand playCommand) {
        for (NetObserver netObserver : netObservers)
            netObserver.playerPlaysCommand(player, playCommand);
    }

    public void connectTo(String host, int port) throws IOException {
        netHandler.connectTo(host, port);
    }

    public void sendMessage(Message message) throws IOException {
        netHandler.sendMessage(message);
    }

    public String getLocalHost() {
        return netHandler.getLocalHost();
    }

    public int getLocalPort() {
        return netHandler.getLocalPort();
    }


}
