package com.andreas.nonblockingrps.view;

public enum ViewPath {
    MAIN_VIEW("/fxml/main_view.fxml"),
    START_VIEW("/fxml/start_view.fxml"),
    CONNECT_VIEW("/fxml/connect_view.fxml");

    public final String name;

    ViewPath(String s) {
        this.name = s;
    }
}
