package main.java.com.andreas.rockpaperscissors.net;

import java.io.Serializable;

public class Peer implements Serializable{
    private final String name;

    public Peer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "[Peer " + name + "]";
    }


    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(Peer.class))
            return false;
        Peer other = (Peer) obj;
        return other.getName().equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
