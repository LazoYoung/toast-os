package toast.ui.controller;

import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import toast.api.Process;
import toast.api.Processor;
import toast.event.ToastEvent;
import toast.event.scheduler.SchedulerStartEvent;
import toast.impl.ToastScheduler;
import toast.persistence.domain.ProcessorRecord;
import toast.persistence.domain.SchedulerRecord;
import toast.ui.view.GanttChart;

import java.util.Optional;

public class GanttChartController {
    private final GanttChart chart;
    private final Canvas canvas;
    private final ToastScheduler scheduler;
    private final int timeSpan = 20;
    private double coreWidth;
    private double rowHeight;
    private double bottomHeight;
    private double timelineWidth;
    private double coreFontSize;
    private double timelineFontSize;

    public GanttChartController(GanttChart chart, Canvas canvas) {
        this.chart = chart;
        this.canvas = canvas;
        this.scheduler = ToastScheduler.getInstance();
        try {
            chart.widthProperty().addListener(e -> chart.setWidth(chart.getWidth()));
            chart.heightProperty().addListener(e -> chart.setHeight(chart.getHeight()));
            resize(canvas.getWidth(), canvas.getHeight());
            addTickListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resize(double width, double height) {
        this.chart.setPrefSize(width, height);
        this.coreWidth = width * 0.15;
        this.rowHeight = height * 0.18;
        this.bottomHeight = rowHeight * 5;
        this.timelineWidth = width - this.coreWidth - 10;
        this.coreFontSize = height * 0.08;
        this.timelineFontSize = height * 0.07;
        this.canvas.setWidth(width);
        this.canvas.setHeight(height);
        repaint();
    }

    private void drawBackground() {
        GraphicsContext g = this.canvas.getGraphicsContext2D();
        double width = this.canvas.getWidth();
        double height = this.canvas.getHeight();
        double xMax = getTimelineX(this.timeSpan);
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setStroke(Color.LIGHTGRAY);
        g.setLineWidth(3);

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

    private void drawCoreIndicators() {
        GraphicsContext g = this.canvas.getGraphicsContext2D();
        double x = this.coreWidth / 2.0;
        g.setFont(Font.font(coreFontSize));
        g.setTextAlign(TextAlignment.CENTER);

        for (Processor processor : this.scheduler.getProcessorList()) {
            if (processor.isActive()) {
                String name = processor.getCore().getName();
                double y = getTimelineY(processor);
                g.setFill(Color.BLUE);
                g.fillRect(2, y, this.coreWidth, this.rowHeight);
                g.setFill(Color.WHITE);
                g.fillText(name, x, y + this.rowHeight / 2.0, this.coreWidth);
            }
        }
    }

    private Color getCoreColor(Process process) {
        double seed = process.getId() * 4321;
        int r = (int) Math.floor(seed % 256);
        int g = (int) Math.floor(seed / 123 % 256);
        int b = (int) Math.floor(seed / 123 % 123 % 256);
        return Color.rgb(r, g, b);
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
        drawProcessBars();
    }

    private void drawProcessBars() {
        double delta = getTimelineDelta();
        GraphicsContext g = this.canvas.getGraphicsContext2D();

        for (Processor processor : ToastScheduler.getInstance().getProcessorList()) {
            ProcessorRecord record = SchedulerRecord.getInstance().getProcessorRecord(processor);
            double timelineY = getTimelineY(processor);

            for (int i = 0; i < this.timeSpan; i++) {
                int time = getTime(i);
                Optional<Process> now = record.getProcessAtTime(time);

                if (now.isPresent()) {
                    g.setFill(getCoreColor(now.get()));
                    g.fillRect(getTimelineX(i), timelineY, delta, this.rowHeight);
                }
            }
        }
    }

    private double getTimelineX(int index) {
        if (index < 0 || index > this.timeSpan) {
            throw new IllegalArgumentException("Timeline index out of range: " + index);
        }

        double delta = getTimelineDelta();
        return this.coreWidth + delta * index;
    }

    private double getTimelineY(Processor processor) {
        return rowHeight * processor.getId();
    }

    private double getTimelineDelta() {
        return timelineWidth / timeSpan;
    }

    private int getTime(int index) {
        if (index < 0 || index > this.timeSpan) {
            throw new IllegalArgumentException("Timeline index out of range: " + index);
        }

        int time = this.scheduler.getElapsedTime();
        int offset = Math.max(time - this.timeSpan, 0);
        return index + offset;
    }

    // todo Repaint every 100 ms
    private void addTickListener() {
        if (this.scheduler.isRunning()) {
            this.scheduler.addTickListener(() -> Platform.runLater(this::repaint));
        } else {
            ToastEvent.registerListener(SchedulerStartEvent.class, (SchedulerStartEvent event) -> {
                this.scheduler.addTickListener(() -> Platform.runLater(this::repaint));
            });
        }
    }

    private void repaint() {
        drawBackground();
        drawCoreIndicators();
        drawTimeline();
    }
}
