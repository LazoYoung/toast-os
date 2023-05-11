package toast.ui.view;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import toast.event.ToastEvent;
import toast.event.scheduler.SchedulerFinishEvent;
import toast.event.scheduler.SchedulerFinishEvent.Cause;
import toast.impl.ToastScheduler;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static javafx.scene.control.Alert.AlertType.*;
import static javafx.scene.control.ButtonType.*;

public class ActionButton extends MFXButton {

    private enum State { START, STOP, RESTART }

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

        changeState(this.scheduler.isRunning() ? State.STOP : State.START);
        setOnMouseClicked(this::onClick);
        ToastEvent.registerListener(SchedulerFinishEvent.class, this::onFinish);
    }

    private void loadImages() {
        this.imageMap.put(State.START, "/toast/images/Simulation/Play.png");
        this.imageMap.put(State.STOP, "/toast/images/Simulation/Pause.png");
        this.imageMap.put(State.RESTART, "/toast/images/Simulation/Restart.png");
    }

    private void changeState(State state) {
        this.state = state;
        URL url = getClass().getResource(imageMap.get(state));
        Image image = new Image(url.toString());
        imageView.setImage(image);
    }

    private void onClick(MouseEvent event) {
        switch (this.state) {
            case START, RESTART -> onStart();
            case STOP -> onStop();
        }
    }

    private void onStart() {
        if (this.scheduler.isConfigured()) {
            startSimulation();
        } else {
            String message = "Please go back to Settings page and fill out the forms.";
            Alert alert = new Alert(WARNING, message, OK);
            alert.setHeaderText("Scheduler is not configured!");
            alert.showAndWait();
        }
    }

    private void onStop() {
        if (this.scheduler.isRunning()) {
            Alert alert = new Alert(ERROR, null, CLOSE);
            alert.setHeaderText("Scheduler is not running!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(CONFIRMATION, null, YES, NO);
            alert.setHeaderText("Confirm you want to terminate this process?");
            alert.showAndWait()
                    .filter(button -> button.equals(YES))
                    .ifPresent((button) -> stopSimulation());
        }
    }

    private void onFinish(ToastEvent event) {
        changeState(State.RESTART);
    }

    private void startSimulation() {
        this.scheduler.start();
        changeState(State.STOP);
    }

    private void stopSimulation() {
        this.scheduler.finish(Cause.COMMAND);
        changeState(State.START);
    }
}
