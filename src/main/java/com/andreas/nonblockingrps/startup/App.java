package com.andreas.nonblockingrps.startup;

import com.andreas.nonblockingrps.view.ViewCoordinator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ViewCoordinator.getInstance().start();
    }
}
