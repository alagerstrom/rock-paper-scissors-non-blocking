package com.andreas.nonblockingrps.model;

import java.io.Serializable;

public class Player implements Serializable {
    private final String displayName;
    private final String uniqueName;

    public Player(String name, String uniqueName) {
        this.displayName = name;
        this.uniqueName = uniqueName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player))
            return false;
        Player otherPlayer = (Player) obj;
        return otherPlayer.uniqueName.equals(uniqueName);
    }

    @Override
    public int hashCode() {
        return uniqueName.hashCode();
    }
}
