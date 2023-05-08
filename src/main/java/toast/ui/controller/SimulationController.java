package toast.ui.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import toast.impl.ToastScheduler;

import java.net.URL;
import java.util.ResourceBundle;

public class SimulationController extends PageController {

    @FXML
    private MFXButton startButton;

    @Override
    void init() {
        startButton.setOnAction(this::onStart);
    }

    @Override
    void exit() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void onStart(ActionEvent event) {
        ToastScheduler scheduler = ToastScheduler.getInstance();

        if (!scheduler.isConfigured()) {
            String message = "Please go back to Settings page and fill out the forms.";
            Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
            alert.setHeaderText("Scheduler is not configured!");
            Platform.runLater(alert::showAndWait);
            return;
        }

        scheduler.start();
    }
}
