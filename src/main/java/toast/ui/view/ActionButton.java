package toast.ui.view;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import toast.event.ToastEvent;
import toast.event.scheduler.SchedulerFinishEvent;
import toast.impl.ToastScheduler;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.WARNING;
import static javafx.scene.control.ButtonType.CLOSE;
import static javafx.scene.control.ButtonType.OK;

public class ActionButton extends MFXButton {

    private enum State { START, PAUSE, RESTART }

    private final ToastScheduler scheduler = ToastScheduler.getInstance();
    private final Map<State, String> imageMap = new HashMap<>();
    private final ImageView imageView = new ImageView();
    private State state;

    public ActionButton() {
        super(null, 50, 50);

        loadImages();
        setGraphic(imageView);
        setBackground(Background.EMPTY);
        imageView.setFitWidth(getPrefWidth());
        imageView.setFitHeight(getPrefHeight());

        changeState(this.scheduler.isStarted() ? State.PAUSE : State.START);
        setOnMouseClicked(this::onClick);
        ToastEvent.registerListener(SchedulerFinishEvent.class, this::onFinish);
    }

    private void loadImages() {
        this.imageMap.put(State.START, "/toast/images/Simulation/Play.png");
        this.imageMap.put(State.PAUSE, "/toast/images/Simulation/Pause.png");
        this.imageMap.put(State.RESTART, "/toast/images/Simulation/Restart.png");
    }

    private void changeState(State state) {
        try {
            this.state = state;
            URL url = getClass().getResource(imageMap.get(state));
            Image image = new Image(url.toString());
            imageView.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onClick(MouseEvent event) {
        switch (this.state) {
            case START, RESTART -> onStart();
            case PAUSE -> onPause();
        }
    }

    private void onStart() {
        if (!this.scheduler.isConfigured()) {
            String message = "Please go back to Settings page and fill out the forms.";
            Alert alert = new Alert(WARNING, message, OK);
            alert.setHeaderText("Scheduler is not configured!");
            alert.showAndWait();
            return;
        }

        if (this.scheduler.isPaused()) {
            this.scheduler.resume();
        } else {
            this.scheduler.start();
        }

        changeState(State.PAUSE);
    }

    private void onPause() {
        if (this.scheduler.isPaused()) {
            Alert alert = new Alert(ERROR, null, CLOSE);
            alert.setHeaderText("Scheduler is not running!");
            alert.showAndWait();
            return;
        }

        this.scheduler.pause();
        changeState(State.START);
    }

    private void onFinish(ToastEvent event) {
        changeState(State.RESTART);
    }
}
