package toast.ui.view;

import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import toast.impl.ToastScheduler;

public class GanttChart extends Pane {

    private final ToastScheduler scheduler;
    private final int timeSpan = 20;
    private final int coreWidth = 150;
    private final int rowHeight = 40;
    private final int cellStroke = 3;
    private int timelineWidth;
    private Canvas canvas;

    public GanttChart(ToastScheduler scheduler) {
        this.scheduler = scheduler;
        scheduler.addTickListener(() -> Platform.runLater(this::updateView));
        createView();
    }

    private void createView() {
        setPrefSize(800, 230);
        this.timelineWidth = (int) (getPrefWidth() - this.coreWidth);
        this.canvas = new Canvas(getPrefWidth(), getPrefHeight());
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
        int xMax = getTimelineX(this.timeSpan);
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setStroke(Color.LIGHTGRAY);
        g.setLineWidth(this.cellStroke);

        g.strokeLine(2, 2, 2, 199);
        g.strokeLine(2, 2, xMax, 2);
        g.strokeLine(2, 199, xMax, 199);

        for (int i = 1; i <= 4; i++) {
            g.strokeLine(2, this.rowHeight * i, xMax, this.rowHeight * i);
        }

        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.setFont(Font.font(16));
        g.setFill(Color.DIMGRAY);
        g.fillText("Arrival Time", this.coreWidth / 2.0, this.rowHeight / 2.0, this.coreWidth);
        g.setFill(Color.BLACK);

        for (int i = 1; i <= 4; i++) {
            g.fillText("OFF", this.coreWidth / 2.0, 20 + this.rowHeight * i, this.coreWidth);
        }
    }

    private void drawTimeline() {
        GraphicsContext g = this.canvas.getGraphicsContext2D();
        g.setStroke(Color.LIGHTGRAY);
        g.setFont(Font.font(14));

        g.setFill(Color.GRAY);
        g.fillText(String.valueOf(getTime(0)), getTimelineX(0), 210);
        g.fillText(String.valueOf(getTime(this.timeSpan)), getTimelineX(this.timeSpan), 210);

        for (int i = 0; i <= this.timeSpan; i++) {
            int x = getTimelineX(i);
            g.fillText(String.valueOf(getTime(i)), x, 210);

            if (i == 0 || i == this.timeSpan) {
                g.setLineWidth(3);
                g.setLineDashes();
            } else {
                g.setLineWidth(1);
                g.setLineDashes(5, 5);
            }

            g.strokeLine(x, 0, x, 199);
        }
    }

    private void drawProcessBar() {
        
    }

    private int getTimelineX(int index) {
        if (index < 0 || index > this.timeSpan) {
            throw new IllegalArgumentException("Timeline index out of range: " + index);
        }

        int delta = this.timelineWidth / this.timeSpan;
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
