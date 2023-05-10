package toast.ui.view;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import toast.api.Algorithm;
import toast.api.Process;
import toast.enums.Palette;

import java.util.Collections;
import java.util.Iterator;

public class ReadyQueue extends ProcessWidget {

    private boolean mission = false;
    private double barWidth;
    private double barHeight;
    private Font processFont;

    public ReadyQueue() {
        super();
    }

    public boolean getMission() {
        return mission;
    }

    public void setMission(boolean mission) {
        this.mission = mission;
    }

    @Override
    protected void onResize(double width, double height) {
        this.barWidth = height - 4;
        this.barHeight = height - 4;
        this.processFont = Font.font(null, FontWeight.BOLD, height * 0.28);
    }

    @Override
    protected void repaint() {
        drawBackground();
        drawProcessBars();
    }

    private void drawBackground() {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        double width = super.canvas.getWidth();
        double height = super.canvas.getHeight();

        g.setFill(Palette.COMPONENT_BACKGROUND.color());
        g.fillRect(0, 0, width, height);

        g.setLineWidth(3);
        g.setStroke(Palette.STROKE.color());
        g.strokeRect(2, 2, width - 4, height - 4);
    }

    private void drawProcessBars() {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        Iterator<Process> iter = getProcessIterator();
        int i = 0;

        g.setFont(this.processFont);
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.setStroke(Palette.STROKE.color());

        while (iter.hasNext()) {
            Process process = iter.next();
            String text = String.valueOf(process.getId());
            Color background = super.getProcessColor(process);
            double x = this.barWidth * i + 2;
            double y = 2;
            double textX = x + this.barWidth / 2;
            double textY = y + this.barHeight / 2;

            g.setFill(background);
            g.fillRect(x, y, this.barWidth, this.barHeight);
            g.strokeRect(x, y, this.barWidth, this.barHeight);
            g.setFill(Palette.getTextColor(background));
            g.fillText(text, textX, textY, this.barWidth);
        }
    }

    private Iterator<Process> getProcessIterator() {
        Algorithm algorithm = super.scheduler.getAlgorithm();

        if (algorithm == null) {
            return Collections.emptyIterator();
        }

        return this.mission ? algorithm.getMissionQueue() : algorithm.getStandardQueue();
    }

}
