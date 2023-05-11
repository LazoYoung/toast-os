package toast.ui.view;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import toast.enums.Metric;
import toast.enums.Palette;
import toast.persistence.domain.SchedulerConfig;

import java.text.DecimalFormat;

public class PerformanceWidget extends ProcessWidget {

    private Metric metric;
    private Font font;
    private double width;
    private double height;
    private double strokeArc;

    public PerformanceWidget() {
        super();
    }

    @Override
    protected void onInit() {
        this.metric = Metric.INVALID;
        this.font = Font.font(null, FontWeight.BOLD, 18);
        this.strokeArc = 10;
    }

    @Override
    protected void onResize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    protected void repaint() {
        switch (this.metric) {
            case ELAPSED_TIME -> drawMetric(super.scheduler.getElapsedTime(), "sec");
            case AVERAGE_TURNAROUND_TIME -> drawMetric(super.scheduler.getAverageTT(), "sec");
            case POWER -> {
                SchedulerConfig config = super.scheduler.getConfig();
                double used = super.scheduler.getPowerConsumed();

                if (super.scheduler.isRunning() && config.getInitPower() != null) {
                    drawBattery(used, config.getInitPower());
                } else {
                    drawMetric(used, "W∙s");
                }
            }
            case INVALID -> drawInoperative();
        }
    }

    public String getMetric() {
        return metric.getIdentifier();
    }

    public void setMetric(String identifier) {
        this.metric = Metric.get(identifier);
    }

    public double getFontSize() {
        return font.getSize();
    }

    public void setFontSize(double size) {
        this.font = Font.font(this.font.getFamily(), FontWeight.BOLD, size);
    }

    private void drawMetric(Number number, String unit) {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        String text = new DecimalFormat("0.#").format(number) + ' ' + unit;
        double x = this.width / 2;
        double y = this.height / 2;
        g.setFont(font);
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.setFill(Palette.TEXT_BLACK.color());
        g.fillText(text, x, y, this.width);
    }

    private void drawInoperative() {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        double x = this.width / 2;
        double y = this.height / 2;
        g.setFont(font);
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.setFill(Palette.TEXT_BLACK.color());
        g.fillText("N/A", x, y, this.width);
    }

    private void drawBattery(double used, double total) {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        double margin = super.scheduler.getConfig().getPowerThreshold();
        double power = 1 - (used / total);
        double barWidth = this.width * power;
        Color barColor;

        if (power < 0.2) {
            barColor = Color.ORANGERED;
        } else if (power < margin) {
            barColor = Color.YELLOW;
        } else {
            barColor = Color.LIGHTGREEN;
        }
        
        g.setFill(Palette.COMPONENT_BACKGROUND.color());
        g.fillRoundRect(0, 0, this.width, this.height, this.strokeArc, this.strokeArc);

        g.setFill(barColor);
        g.fillRoundRect(0, 0, barWidth, this.height, this.strokeArc, this.strokeArc);
        
        g.setFill(Palette.STROKE_LIGHT.color());
        g.setLineWidth(2);
        g.strokeRoundRect(1, 1, this.width - 2, this.height - 2, this.strokeArc, this.strokeArc);

        drawPowerMetric(Palette.TEXT_BLACK.color(), power * 100);
    }

    private void drawPowerMetric(Color textColor, double percentage) {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        String text = String.format("%.0f%%", percentage);

        g.setFont(this.font);
        g.setFill(textColor);
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.fillText(text, this.width / 2, this.height / 2, this.width - 10);
    }
}
