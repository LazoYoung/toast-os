package toast.ui.controller;

import java.net.URL;
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

    private static PageController befController;
    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changePage(MAIN);
    }

    private void changePage(String fileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/toast/fxml/" + fileName + ".fxml"));

            Parent root = fxmlLoader.load();

            if (befController != null) {
                befController.exit();
            }
            PageController controller = fxmlLoader.getController();
            befController = controller;
            controller.init();

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
