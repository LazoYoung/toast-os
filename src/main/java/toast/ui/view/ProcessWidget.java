package toast.ui.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import toast.api.Process;
import toast.enums.Palette;
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

        if (ToastScheduler.getInstance().isStarted()) {
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

    private static int missionColorSeq = 0;
    private static int notMissionColorSeq = 0;

    public static Color getMissionColor() {
        int ret = missionColorSeq++;

        missionColorSeq %= missionColors.size();
        return missionColors.get(ret);
    }

    public static Color getNotMissionColor() {
        int ret = notMissionColorSeq++;

        notMissionColorSeq %= notMissionColors.size();

        return notMissionColors.get(ret);
    }

    public static final List<Color> missionColors = List.of(
            Palette.PROCESS_MISSION_1.color(),
            Palette.PROCESS_MISSION_2.color(),
            Palette.PROCESS_MISSION_3.color(),
            Palette.PROCESS_MISSION_4.color(),
            Palette.PROCESS_MISSION_5.color(),
            Palette.PROCESS_MISSION_6.color()
    );
    public static final List<Color> notMissionColors = List.of(
            Palette.PROCESS_NOT_MISSION_1.color(),
            Palette.PROCESS_NOT_MISSION_2.color(),
            Palette.PROCESS_NOT_MISSION_3.color(),
            Palette.PROCESS_NOT_MISSION_4.color(),
            Palette.PROCESS_NOT_MISSION_5.color(),
            Palette.PROCESS_NOT_MISSION_6.color()
    );

    private static Color getColor(Process process) {
        if(hashData.containsKey(process.getId())) {
            return hashData.get(process.getId());
        }

        Color color = process.isMission() ? getMissionColor() : getNotMissionColor();
        hashData.put(process.getId(), color);
        return color;
    }

    private static final Map<Integer, Color> hashData = new HashMap<>();

    protected Color getProcessColor(Process process) {
        return getColor(process);
//        double saturation = (double) ((this.seed * process.getId()) % this.variation) / this.variation;
//        return color.deriveColor(1.0, saturation, 1.0, 1.0);
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
        try {
            GraphicsContext g = this.canvas.getGraphicsContext2D();
            g.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
            repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
