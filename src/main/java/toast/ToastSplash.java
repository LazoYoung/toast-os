package toast;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ToastSplash extends Preloader {

    private final int delay = 3000;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
//        primaryStage.initStyle(StageStyle.UNDECORATED);

        Pane borderPane = new Pane();
        borderPane.setStyle("-fx-background-color: black;");

        primaryStage.setScene(new Scene(borderPane, 300, 250));
        primaryStage.show();

        Thread.sleep(2000);
    }

    private Scene createScene() {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 500, 300);
        root.setStyle("-fx-background-color: black;");
        scene.setFill(Color.BLACK);
        return scene;
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }
}
