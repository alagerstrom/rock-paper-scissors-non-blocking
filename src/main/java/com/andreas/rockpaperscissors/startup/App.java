package main.java.com.andreas.rockpaperscissors.startup;

import com.andreas.rockpaperscissors.view.ViewCoordinator;
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
        com.apple.eawt.Application application = com.apple.eawt.Application.getApplication();
        application.setQuitHandler((quitEvent, quitResponse) -> Platform.exit());
    }
}
