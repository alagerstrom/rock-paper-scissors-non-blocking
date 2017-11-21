package com.andreas.nonblockingrps.view;

public enum ImagePath {
    PAPER("/images/paper.png"),
    ROCK("/images/rock.png"),
    SCISSORS("/images/scissors.png"),
    MAIN_BACKGROUND("/images/background2.jpg"),
    CONNECT("/images/connect.jpg");

    String name;



    ImagePath(String s) {
        this.name = s;
    }

    @Override
    public String toString() {
        return name;
    }
}
