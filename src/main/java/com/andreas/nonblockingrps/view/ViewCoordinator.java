package com.andreas.nonblockingrps.view;

import com.andreas.nonblockingrps.util.Constants;
import com.andreas.nonblockingrps.util.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewCoordinator {
    private static ViewCoordinator instance = new ViewCoordinator();

    private ViewCoordinator() {
    }

    public static ViewCoordinator getInstance() {
        return instance;
    }

    public void start() throws IOException {
        showView(ViewPath.START_VIEW);
    }

    public void start(Stage primaryStage) throws IOException {
        showView(ViewPath.START_VIEW, primaryStage);
    }

    public void hideWindow(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        node.getScene().getWindow().hide();
    }

    public void showMainView() {
        Stage stage = showView(ViewPath.MAIN_VIEW);
        stage.setOnCloseRequest(event -> {
            Logger.log("Running platform.exit()");
            Platform.exit();
        });
    }

    public Stage showView(ViewPath viewPath) {
        Stage stage = new Stage();
        showView(viewPath, stage);
        return stage;
    }

    private void showView(ViewPath viewPath, Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath.name));
        Parent root;
        try {
            root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style/main.css").toString());
            stage.setScene(scene);
            stage.setTitle(Constants.WINDOW_TITLE);
            stage.show();
        } catch (IOException e) {
            Logger.log("Failed to show view " + viewPath);
        }
    }
}
