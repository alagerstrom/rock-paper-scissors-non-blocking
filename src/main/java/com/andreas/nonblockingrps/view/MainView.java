package main.java.com.andreas.rockpaperscissors.view;

import com.andreas.nonblockingrps.controller.AppController;
import com.andreas.nonblockingrps.model.GameObserver;
import com.andreas.nonblockingrps.util.Constants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import com.andreas.nonblockingrps.util.Logger;

import java.nio.channels.CompletionHandler;
import java.util.List;

public class MainView {

    private AppController appController = AppController.getInstance();

    @FXML
    TextField messageField;
    @FXML
    TextArea messages;
    @FXML
    ListView playerList;
    @FXML
    Text totalText, roundText, ipText, portText, nameText, statusText;
    @FXML
    Button rockButton, scissorsButton, paperButton;
    @FXML
    GridPane gridPane;
    @FXML
    BorderPane borderPane;


    public void initialize() {
        printMessage("Rock Paper Scissors game started");
        Logger.log("Main view initialized");

        initializeButtons();
        initializePlayerName();

        statusText.setText("Welcome, make your choice!");

        updateScoreText(0, 0);

        messages.setFocusTraversable(false);
        messages.setEditable(false);

        appController.addGameObserver(new GameHandler());
        initializeIpAndPortTexts();
        appController.sendPlayerInfo(null);
    }

    private void initializePlayerName() {
        appController.getPlayerName(new CompletionHandler<String, Void>() {
            @Override
            public void completed(String result, Void attachment) {
                Platform.runLater(()->{
                    nameText.setText(result);
                });
            }

            @Override
            public void failed(Throwable exc, Void attachment) { }
        });
    }

    private void initializeButtons() {
        double scaleImage = 0.6;
        ImageView paper = new ImageView(getClass().getResource(ImagePath.PAPER.name).toString());
        paper.setPreserveRatio(true);
        paper.setFitWidth(paperButton.getPrefWidth() * scaleImage);
        paperButton.setGraphic(paper);
        paperButton.setText("");

        ImageView rock = new ImageView(getClass().getResource(ImagePath.ROCK.name).toString());
        rock.setPreserveRatio(true);
        rock.setFitWidth(rockButton.getPrefWidth() * scaleImage);
        rockButton.setGraphic(rock);
        rockButton.setText("");

        ImageView scissors = new ImageView(getClass().getResource(ImagePath.SCISSORS.name).toString());
        scissors.setPreserveRatio(true);
        scissors.setFitWidth(scissorsButton.getPrefWidth() * scaleImage);
        scissorsButton.setGraphic(scissors);
        scissorsButton.setText("");


    }

    private void updateScoreText(int roundScore, int totalScore) {
        totalText.setText(Constants.TOTAL_PREFIX + totalScore);
        roundText.setText(Constants.ROUND_PREFIX + roundScore);
    }


    @FXML
    public void sendChatMessage() {
        String messageContent = messageField.getText();
        appController.sendChatMessage(messageContent, new CompletionHandler<Void, Void>() {
            @Override
            public void completed(Void result, Void attachment) {
                messageField.setText("");
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                Logger.log("Failed to send chat message");
            }
        });
    }

    private synchronized void printMessage(String message) {
        messages.appendText(message + "\n");
    }

    private void initializeIpAndPortTexts() {
        appController.getLocalPort(new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                Platform.runLater(()->portText.setText("" + result));
            }

            @Override
            public void failed(Throwable exc, Void attachment) { }
        });
        ipText.setText("[Getting...]");
        appController.getLocalHost(new CompletionHandler<String, Void>() {
            @Override
            public void completed(String result, Void attachment) {
                Platform.runLater(()->ipText.setText(result));
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                Platform.runLater(()->ipText.setText("[No IP]"));
            }
        });
    }

    private class PlayCompletionHandler implements CompletionHandler<Void, Void>{
        @Override
        public void completed(Void result, Void attachment) { }

        @Override
        public void failed(Throwable exc, Void attachment) {
            Logger.log("Failed to send");
            Platform.runLater(()->enableButtons());
        }
    }

    @FXML
    public void playRock() {
        disableButtons();
        statusText.setText("You played Rock!");
        appController.sendPlayRock(new PlayCompletionHandler());
    }

    @FXML
    public void playPaper() {
        disableButtons();
        statusText.setText("You played Paper!");
        appController.sendPlayPaper(new PlayCompletionHandler());
    }

    @FXML
    public void playScissors() {
        disableButtons();
        statusText.setText("You played Scissors!");
        appController.sendPlayScissors(new PlayCompletionHandler());
    }

    @FXML
    private void connectButtonClicked() {
        ViewCoordinator.getInstance().showView(ViewPath.CONNECT_VIEW);
    }

    private class GameHandler implements GameObserver {

        @Override
        public void allPlayers(List<String> allPlayers) {
            Platform.runLater(() -> {
                playerList.getItems().clear();
                playerList.getItems().addAll(allPlayers);
            });
        }

        @Override
        public void playerJoinedTheGame(String player) {
            Platform.runLater(() -> printMessage(player + " joined the game."));
        }

        @Override
        public void playerLeftTheGame(String player) {
            Platform.runLater(() -> printMessage(player + " left the game."));
        }

        @Override
        public void chatMessage(String message) {
            Platform.runLater(() -> printMessage(message));
        }

        @Override
        public void draw() {
            Platform.runLater(()-> statusText.setText("Draw!"));
        }

        @Override
        public void victory(int roundScore, int totalScore) {
            Platform.runLater(() -> {
                updateScoreText(roundScore, totalScore);
                statusText.setText("You win!");
            });

        }

        @Override
        public void loss() {
            Platform.runLater(() -> statusText.setText("You loose!"));

        }

        @Override
        public void newRound(int totalScore) {
            Platform.runLater(()->{
                enableButtons();
                updateScoreText(0, totalScore);
                statusText.setText("Make your choice!");
            });
        }
    }

    private void enableButtons() {
        rockButton.setDisable(false);
        paperButton.setDisable(false);
        scissorsButton.setDisable(false);
    }
    private void disableButtons(){
        rockButton.setDisable(true);
        paperButton.setDisable(true);
        scissorsButton.setDisable(true);
    }
}
