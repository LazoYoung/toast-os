package toast.ui.view;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import toast.event.ToastEvent;
import toast.event.scheduler.SchedulerFinishEvent;
import toast.event.scheduler.SchedulerStartEvent;
import toast.impl.ToastScheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class CanvasWidget extends Pane {

    protected final ToastScheduler scheduler;
    protected final Canvas canvas;
    private ScheduledFuture<?> thread = null;

    public CanvasWidget() {
        this.scheduler = ToastScheduler.getInstance();
        this.canvas = createCanvas();

        init();
        resizeWidget(this.canvas.getWidth(), this.canvas.getHeight());
        widthProperty().addListener(e -> setWidth(getWidth()));
        heightProperty().addListener(e -> setHeight(getHeight()));
        ToastEvent.registerListener(SchedulerStartEvent.class, (e) -> startPainting());
        ToastEvent.registerListener(SchedulerFinishEvent.class, (e) -> stopPainting());

        if (ToastScheduler.getInstance().isRunning()) {
            startPainting();
        }
    }

    @Override
    public void setWidth(double width) {
        super.setWidth(width);
        resizeWidget(width, getPrefHeight());
    }

    @Override
    public void setHeight(double height) {
        super.setHeight(height);
        resizeWidget(getPrefWidth(), height);
    }

    /**
     * This method is called when this widget or the scheduler is reloaded
     */
    protected abstract void init();

    /**
     * This method is called when this widget is resized
     * @param width New width
     * @param height New height
     */
    protected abstract void onResize(double width, double height);

    /**
     * This method is repeatedly called every 100ms to repaint the widget
     */
    protected abstract void repaint();

    private void resizeWidget(double width, double height) {
        this.canvas.setWidth(width);
        this.canvas.setHeight(height);
        setPrefSize(width, height);
        onResize(width, height);
        repaint();
    }

    private void startPainting() {
        long period = 100L;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        this.thread = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> Platform.runLater(this::repaint), 0L, period, unit);
    }

    private void stopPainting() {
        this.thread.cancel(false);
    }

    private Canvas createCanvas() {
        double width = 800;
        double height = 230;
        Canvas canvas = new Canvas(width, height);
        setPrefSize(width, height);
        getChildren().add(canvas);
        return canvas;
    }
}
