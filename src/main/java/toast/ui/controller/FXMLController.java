package toast.ui.controller;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class FXMLController implements Initializable {

    public static final String MAIN = "Main";
    public static final String SETTING = "Setting";
    public static final String RUN_AND_RESULT = "RunAndResult";
    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changePage(MAIN);
    }

    private void changePage(String fileName) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/toast/fxml/" + fileName + ".fxml")));
            //            Object Controller = new FXMLLoader(resource).getController();
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            System.out.println("ERR WITH PAGE : " + fileName);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void main() {
        changePage(MAIN);
    }

    public void setting() {
        changePage(SETTING);
    }

    public void runAndResult() {
        changePage(RUN_AND_RESULT);
    }
}
