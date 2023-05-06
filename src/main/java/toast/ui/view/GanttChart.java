package toast.ui.view;

import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import toast.event.ToastEvent;
import toast.event.scheduler.SchedulerStartEvent;
import toast.impl.ToastScheduler;

public class GanttChart extends Pane {

    private final ToastScheduler scheduler;
    private final int timeSpan = 20;
    private final int cellStroke = 3;
    private double coreWidth;
    private double rowHeight;
    private double bottomHeight;
    private double timelineWidth;
    private double coreFontSize;
    private double timelineFontSize;
    private Canvas canvas;

    public GanttChart() {
        scheduler = ToastScheduler.getInstance();
        ToastEvent.registerListener(SchedulerStartEvent.class, (SchedulerStartEvent event) -> {
            scheduler.addTickListener(() -> Platform.runLater(this::updateView));
        });
        createView();
    }

    public void setWidth(double width) {
        setSize(width, getPrefHeight());
    }

    public void setHeight(double height) {
        setSize(getPrefWidth(), height);
    }

    private void setSize(double width, double height) {
        setPrefSize(width, height);
        this.coreWidth = getPrefWidth() * 0.15;
        this.rowHeight = getPrefHeight() * 0.18;
        this.bottomHeight = rowHeight * 5;
        this.timelineWidth = getPrefWidth() - this.coreWidth - 10;
        this.coreFontSize = height * 0.08;
        this.timelineFontSize = height * 0.07;
        this.canvas.setWidth(width);
        this.canvas.setHeight(height);
        repaint();
    }

    private void createView() {
        double width = 800;
        double height = 230;
        this.canvas = new Canvas();
        setSize(width, height);
        getChildren().add(this.canvas);
        repaint();
    }

    private void repaint() {
        drawBackground();
        drawTimeline();
    }

    private void drawBackground() {
        GraphicsContext g = this.canvas.getGraphicsContext2D();
        double width = this.canvas.getWidth();
        double height = this.canvas.getHeight();
        double xMax = getTimelineX(this.timeSpan);
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setStroke(Color.LIGHTGRAY);
        g.setLineWidth(this.cellStroke);

        g.strokeLine(2, 2, 2, bottomHeight);
        g.strokeLine(2, 2, xMax, 2);
        g.strokeLine(2, bottomHeight, xMax, bottomHeight);

        for (int i = 1; i <= 4; i++) {
            g.strokeLine(2, this.rowHeight * i, xMax, this.rowHeight * i);
        }

        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.setFont(Font.font(coreFontSize));
        g.setFill(Color.DIMGRAY);
        g.fillText("Arrival Time", this.coreWidth / 2.0, this.rowHeight / 2.0, this.coreWidth);
        g.setFill(Color.BLACK);

        for (int i = 1; i <= 4; i++) {
            g.fillText("OFF", this.coreWidth / 2.0, (2 * i + 1) * this.rowHeight / 2.0, this.coreWidth);
        }
    }

    private void drawTimeline() {
        GraphicsContext g = this.canvas.getGraphicsContext2D();
        g.setStroke(Color.LIGHTGRAY);
        g.setFont(Font.font(timelineFontSize));

        g.setFill(Color.GRAY);
        g.fillText(String.valueOf(getTime(0)), getTimelineX(0), bottomHeight + 10);
        g.fillText(String.valueOf(getTime(this.timeSpan)), getTimelineX(this.timeSpan), bottomHeight + 10);

        for (int i = 0; i <= this.timeSpan; i++) {
            double x = getTimelineX(i);
            g.fillText(String.valueOf(getTime(i)), x, bottomHeight + 10);

            if (i == 0 || i == this.timeSpan) {
                g.setLineWidth(3);
                g.setLineDashes();
            } else {
                g.setLineWidth(1);
                g.setLineDashes(5, 5);
            }

            g.strokeLine(x, 0, x, bottomHeight);
        }
    }

    private void drawProcessBar() {
        // todo method stub
    }

    private double getTimelineX(int index) {
        if (index < 0 || index > this.timeSpan) {
            throw new IllegalArgumentException("Timeline index out of range: " + index);
        }

        double delta = this.timelineWidth / this.timeSpan;
        return this.coreWidth + delta * index;
    }

    private int getTime(int index) {
        if (index < 0 || index > this.timeSpan) {
            throw new IllegalArgumentException("Timeline index out of range: " + index);
        }

        int offset = Math.max(this.scheduler.getElapsedTime() - this.timeSpan, 0);
        return index + offset;
    }

    private void updateView() {
        int time = this.scheduler.getElapsedTime();
        System.out.println("Current time: " + time);

        repaint();
    }
}
