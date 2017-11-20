package main.java.com.andreas.rockpaperscissors.view;

import com.andreas.rockpaperscissors.controller.AppController;
import com.andreas.rockpaperscissors.util.Constants;
import com.andreas.rockpaperscissors.util.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.nio.channels.CompletionHandler;

public class StartView {

    @FXML
    TextField nameField;
    @FXML
    TextField portField;
    @FXML
    Text errorText;
    @FXML
    ProgressBar startProgress;
    @FXML
    GridPane startGrid;

    public void initialize() {
        portField.setText("" + Constants.DEFAULT_PORT);
        errorText.setText("");
        Platform.runLater(() -> nameField.requestFocus());
        Logger.log("Start view initialized");
    }

    @FXML
    public void startGame(ActionEvent actionEvent) throws IOException {
        String playerName = nameField.getText();
        String portNumber = portField.getText();

        Logger.log("Name: " + playerName);
        int port;
        try {
            port = Integer.parseInt(portNumber);
        } catch (NumberFormatException e) {
            errorText.setText("Invalid port number");
            return;
        }

        sendStartGameRequest(playerName, port, actionEvent);

    }

    private void sendStartGameRequest(String playerName, int port, ActionEvent actionEvent) {
        errorText.setText("Starting game...");
        disableControlsAndShowProgressBar();
        AppController.getInstance().createNewGame(playerName, port, new CompletionHandler<Void, Void>() {
            @Override
            public void completed(Void result, Void attachment) {
                Platform.runLater(() -> {
                    ViewCoordinator viewCoordinator = ViewCoordinator.getInstance();
                    viewCoordinator.showView(ViewPath.MAIN_VIEW);
                    viewCoordinator.showView(ViewPath.CONNECT_VIEW);
                    viewCoordinator.hideWindow(actionEvent);
                });
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                errorText.setText("Failed to use that port, try another one.");
                enableControlsAndHideProgressBar();
            }
        });
    }

    private void enableControlsAndHideProgressBar() {
        startGrid.setDisable(false);
        startProgress.setVisible(false);
    }

    private void disableControlsAndShowProgressBar() {
        startGrid.setDisable(true);
        startProgress.setVisible(true);
    }
}
