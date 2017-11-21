package main.java.com.andreas.rockpaperscissors.view;

import com.andreas.nonblockingrps.util.Constants;
import com.andreas.nonblockingrps.util.Logger;
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

    public void hideWindow(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        node.getScene().getWindow().hide();
    }

    public void showView(ViewPath viewPath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath.name));
        Parent root;
        try {
            root = loader.load();
            Stage stage = new Stage();
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
