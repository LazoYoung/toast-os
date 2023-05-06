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

    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changePage("page1");
    }

    private void changePage(String fileName) {
        try {
            Parent fxml = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/toast/fxml/" + fileName + ".fxml")));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(fxml);
        } catch (Exception e) {
            System.out.println("ERR WITH PAGE : " + fileName);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void page1() {
        changePage("page1");
    }

    public void page2() {
        changePage("page2");
    }

    public void page3() {
        changePage("page3");
    }


}
