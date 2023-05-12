package toast;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class SplashController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Executors.newSingleThreadExecutor().submit(this::splash);
    }

    private void splash() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            ToastApp.splashStage.close();

            try {
                Parent parent = FXMLLoader.load(getClass().getResource("scene.fxml"));
                parent.getStylesheets().add(getClass().getResource("/toast/styles.css").toExternalForm());
                Stage stage = new Stage(StageStyle.DECORATED);
                Scene scene = new Scene(parent);
                stage.setScene(scene);
                stage.setTitle("Orbit");
                Image icon = new Image("/toast/images/Misc/icon.png");
                stage.getIcons().add(icon);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
