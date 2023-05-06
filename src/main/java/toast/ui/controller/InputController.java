package toast.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import toast.enums.AlgorithmName;

public class InputController implements Initializable {

    @FXML
    private Label algorithmLabel;

    @FXML
    private ChoiceBox<AlgorithmName> algorithmNameChoiceBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithmNameChoiceBox.getItems().addAll(AlgorithmName.values());
    }
}
