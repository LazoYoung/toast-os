package toast.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import toast.enums.Page;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {

    private static PageController befController;
    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changePage(Page.HOME);
    }

    private void changePage(Page page) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(page.getLocation()));

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
            System.out.println("ERROR WITH PAGE: " + page.getLocation());
            e.printStackTrace();
        }
    }

    public void toHomePage() {
        changePage(Page.HOME);
    }

    public void toSettingsPage() {
        changePage(Page.SETTINGS);
    }

    public void toSimulationPage() {
        changePage(Page.SIMULATION);
    }
}
