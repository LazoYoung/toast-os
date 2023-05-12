package toast.ui.view;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import toast.api.Core;
import toast.api.Process;
import toast.api.Processor;
import toast.enums.Palette;
import toast.persistence.domain.FlagRecord;
import toast.persistence.domain.ProcessorRecord;
import toast.persistence.domain.SchedulerRecord;

import java.util.Optional;

import static toast.enums.Palette.*;

public class GanttChart extends ProcessWidget {

    private int timeSpan;
    private double coreWidth;
    private double rowHeight;
    private double bottomHeight;
    private double timelineWidth;
    private Font coreFont;
    private Font processFont;
    private Font timelineFont;

    public GanttChart() {
        super();
    }

    @Override
    protected void onInit() {
        this.timeSpan = 20;
    }

    @Override
    protected void onResize(double width, double height) {
        this.coreWidth = width * 0.15;
        this.rowHeight = height * 0.18;
        this.bottomHeight = rowHeight * 5;
        this.timelineWidth = width - this.coreWidth - 10;
        this.coreFont = Font.font(null, FontWeight.BOLD, height * 0.08);
        this.processFont = Font.font(null, FontWeight.BOLD, height * 0.07);
        this.timelineFont = Font.font(null, FontWeight.NORMAL, height * 0.07);
    }

    @Override
    protected void repaint() {
        drawBackground();
        drawTimeline();
        drawProcessBars();
        drawCoreIndicators();
        drawFlags();
    }

    private void drawBackground() {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        double width = super.canvas.getWidth();
        double height = super.canvas.getHeight();
        double xMax = getTimelineX(this.timeSpan);

        g.setFill(COMPONENT_BACKGROUND.color());
        g.fillRect(0, 0, width, height);

        g.setLineWidth(3);
        g.setStroke(STROKE.color());
        g.strokeLine(2, 2, 2, this.bottomHeight);
        g.strokeLine(2, 2, xMax, 2);
        g.strokeLine(2, this.bottomHeight, xMax, this.bottomHeight);

        for (int i = 1; i <= 4; i++) {
            g.strokeLine(2, this.rowHeight * i, xMax, this.rowHeight * i);
        }

        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.setFont(this.coreFont);
        g.setFill(TEXT_WIDGET.color());
        g.fillText("Arrival Time", this.coreWidth / 2.0, this.rowHeight / 2.0, this.coreWidth);
        g.setFill(TEXT_DISABLED.color());

        for (int i = 1; i <= 4; i++) {
            g.fillText("OFF", this.coreWidth / 2.0, (2 * i + 1) * this.rowHeight / 2.0, this.coreWidth);
        }
    }

    private void drawCoreIndicators() {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        double x = this.coreWidth / 2.0;
        g.setFont(this.coreFont);
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);

        for (Processor processor : super.scheduler.getProcessorList()) {
            if (processor.isActive()) {
                Core core = processor.getCore();
                String name = core.getName();
                double y = getTimelineY(processor);
                g.setFill((core == Core.PERFORMANCE) ? P_CORE.color() : E_CORE.color());
                g.fillRect(3, y, this.coreWidth - 3, this.rowHeight);
                g.setFill(TEXT_WHITE.color());
                g.fillText(name, x, y + this.rowHeight / 2.0, this.coreWidth);
            }
        }
    }

    private void drawTimeline() {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        g.setFont(this.timelineFont);
        g.setFill(TEXT_WIDGET.color());

        g.fillText(String.valueOf(getTime(0)), getTimelineX(0), this.bottomHeight + 10);
        g.fillText(String.valueOf(getTime(this.timeSpan)), getTimelineX(this.timeSpan), this.bottomHeight + 10);

        for (int i = 0; i <= this.timeSpan; i++) {
            double x = getTimelineX(i);
            g.fillText(String.valueOf(getTime(i)), x, this.bottomHeight + 10);

            if (i == 0 || i == this.timeSpan) {
                g.setStroke(STROKE.color());
                g.setLineWidth(3);
                g.setLineDashes();
            } else {
                g.setStroke(STROKE_LIGHT.color());
                g.setLineWidth(1);
                g.setLineDashes(5, 5);
            }

            g.strokeLine(x, 0, x, this.bottomHeight);
        }
    }

    private void drawProcessBars() {
        double delta = getTimelineDelta();
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        g.setFont(this.processFont);
        g.setStroke(Palette.STROKE.color());

        for (Processor processor : super.scheduler.getProcessorList()) {
            ProcessorRecord record = SchedulerRecord.getInstance().getProcessorRecord(processor);
            Optional<Process> prev = Optional.empty();
            double timelineY = getTimelineY(processor);
            int length = 0;

            for (int i = 0; i <= this.timeSpan; i++) {
                Optional<Process> now = record.getProcessAtTime(getTime(i));

                if (prev.isPresent() && !prev.equals(now)) {
                    double timelineX = getTimelineX(i) - delta * length;
                    double barWidth = delta * length;
                    double textX = getTimelineX(i) - delta * length / 2;
                    double textY = timelineY + this.rowHeight / 2;
                    Color barColor = super.getProcessColor(prev.get());
                    String text = String.valueOf(prev.get().getId());
                    g.setFill(barColor);
                    g.fillRect(timelineX, timelineY, barWidth, this.rowHeight);
                    g.strokeRect(timelineX, timelineY, barWidth, this.rowHeight);
                    g.setFill(Palette.getTextColor(barColor));
                    g.setTextAlign(TextAlignment.CENTER);
                    g.fillText(text, textX, textY, delta * length);
                    length = 0;
                }

                if (now.isPresent()) {
                    length++;
                }

                prev = now;
            }
        }
    }

    private void drawFlags() {
        FlagRecord record = SchedulerRecord.getInstance().getFlagRecord();

        for (int i = 0; i < this.timeSpan; i++) {
            final int time = getTime(i);

            record.getFlagAtTime(time).ifPresent((process) -> {
                drawFlag(process, time);
            });
        }
    }

    private void drawFlag(Process process, int time) {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        double timelineX = getTimelineX(getIndex(time));
        double x0 = timelineX;
        double y0 = 3;
        double x1 = x0;
        double y1 = rowHeight;
        double x2 = timelineX + rowHeight;
        double y2 = 3;

        g.setStroke(STROKE.color());
        g.setLineWidth(3);
        g.strokeLine(x0, y0, x1, y1);
        g.strokeLine(x0, y0, x2, y2);
        g.strokeLine(x1, y1, x2, y2);

        Color color = getProcessColor(process);
        g.setFill(color);
        g.fillPolygon(new double[]{x0, x1, x2}, new double[]{y0, y1, y2}, 3);

        double offset = rowHeight * 0.3;
        g.setFont(processFont);
        g.setFill(Palette.getTextColor(color));
        g.fillText(String.valueOf(process.getId()), x0 + offset, y0 + offset);
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

        int time = super.scheduler.getElapsedTime();
        int offset = Math.max(time - this.timeSpan, 0);
        return index + offset;
    }

    private int getIndex(int time) {
        return Math.min(time, this.timeSpan);
    }
}
