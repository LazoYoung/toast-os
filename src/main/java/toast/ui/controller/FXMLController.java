package toast.ui.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.List;
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

    @FXML
    private MFXButton homeButton;
    @FXML
    private MFXButton settingButton;
    @FXML
    private MFXButton simulationButton;

    private List<MFXButton> buttons;

    private int currIdx = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttons = List.of(homeButton, settingButton, simulationButton);
        changePage(Page.HOME);

    }

    private void changeCurrPage(int nextIdx) {
         buttons.get(currIdx).setStyle(
                 "-fx-background-color: #333F50; "
                 );
         buttons.get(nextIdx).setStyle(
                 "-fx-background-color: #222A35; " +
                 "-fx-border-style: none solid none none; -fx-border-width: 0 5 0 0; -fx-border-color: #0926FF;"

         );

         currIdx = nextIdx;
    }

    private void changePage(Page page) {
        changeCurrPage(page.ordinal());
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
