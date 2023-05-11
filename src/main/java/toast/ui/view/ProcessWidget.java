package toast.ui.view;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import toast.api.Process;
import toast.event.ToastEvent;
import toast.event.scheduler.SchedulerFinishEvent;
import toast.event.scheduler.SchedulerStartEvent;
import toast.impl.ToastScheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static toast.enums.Palette.E_CORE;
import static toast.enums.Palette.P_CORE;

public abstract class ProcessWidget extends Pane {

    protected final ToastScheduler scheduler;
    protected final Canvas canvas;
    private ScheduledFuture<?> thread = null;
    private int variation;
    private int seed;

    public ProcessWidget() {
        this.scheduler = ToastScheduler.getInstance();
        this.canvas = createCanvas();

        onInit();
        reloadProperties();
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
     * This method is called at instantiation phase
     */
    protected abstract void onInit();

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

    protected Color getProcessColor(Process process) {
        Color color = process.isMission() ? P_CORE.color() : E_CORE.color();
        double saturation = (double) ((this.seed * process.getId()) % this.variation) / this.variation;
        return color.deriveColor(1.0, saturation, 1.0, 1.0);
    }

    private void reloadProperties() {
        this.variation = Math.max(5, this.scheduler.getProcessList().size());
        this.seed = ThreadLocalRandom.current().nextInt(this.variation);
    }

    private void resizeWidget(double width, double height) {
        this.canvas.setWidth(width);
        this.canvas.setHeight(height);
        setPrefSize(width, height);
        onResize(width, height);
        clearAndPaint();
    }

    private void startPainting() {
        reloadProperties();
        long period = 100L;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        this.thread = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::repaintLater, 0L, period, unit);
    }

    private void stopPainting() {
        this.thread.cancel(false);
        clearAndPaint();
    }

    private void repaintLater() {
        Platform.runLater(this::clearAndPaint);
    }

    private void clearAndPaint() {
        GraphicsContext g = this.canvas.getGraphicsContext2D();
        g.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
        repaint();
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
