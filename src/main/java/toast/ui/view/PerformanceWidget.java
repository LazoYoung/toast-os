package toast.ui.view;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import toast.enums.Metric;
import toast.enums.Palette;
import toast.persistence.domain.SchedulerConfig;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class PerformanceWidget extends ProcessWidget {

    private static final Map<Metric, Image> imageMap = new HashMap<>();
    private static final Map<Metric, String> titleMap = new HashMap<>();

    static {
        imageMap.put(Metric.RUN_TIME, createImage("/toast/images/Simulation/Run-time.png"));
        imageMap.put(Metric.POWER, createImage("/toast/images/Simulation/Power.png"));
        imageMap.put(Metric.AVERAGE_TT, createImage("/toast/images/Simulation/TT.png"));
        titleMap.put(Metric.RUN_TIME, "Total runtime");
        titleMap.put(Metric.POWER, "Power used");
        titleMap.put(Metric.AVERAGE_TT, "Average TT");
    }

    private static Image createImage(String path) {
        URL url = PerformanceWidget.class.getResource(path);
        return new Image(url.toString());
    }

    private Metric metric;
    private Font headerFont;
    private Font metricFont;
    private double width;
    private double height;
    private double strokeArc;
    private double iconSize;
    private double margin;
    private double textWidth;
    private double metricY;

    public PerformanceWidget() {
        super();
    }

    @Override
    protected void onInit() {
        this.metric = Metric.INVALID;
        this.headerFont = Font.font(null, FontWeight.BOLD, 14);
        this.metricFont = Font.font(null, FontWeight.BOLD, 18);
        this.strokeArc = 10;
        this.iconSize = 15;
        this.margin = 5;
        this.metricY = 50;
    }

    @Override
    protected void onResize(double width, double height) {
        this.width = width;
        this.height = height;
        this.textWidth = 90;
    }

    @Override
    protected void repaint() {
        drawHeader();

        switch (this.metric) {
            case RUN_TIME -> drawMetric(super.scheduler.getElapsedTime(), "sec");
            case AVERAGE_TT -> drawMetric(super.scheduler.getAverageTT(), "sec");
            case POWER -> {
                SchedulerConfig config = super.scheduler.getConfig();
                double used = super.scheduler.getPowerConsumed();

                if (super.scheduler.isStarted() && config.getInitPower() != null) {
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

    private void drawHeader() {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        double textX = this.width / 2;
        double imgX = textX - this.textWidth / 2 - this.iconSize - margin;
        g.drawImage(imageMap.get(this.metric), imgX, 0, this.iconSize, this.iconSize);
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.TOP);
        g.setFont(this.headerFont);
        g.setFill(Palette.TEXT_WIDGET.color());
        g.fillText(titleMap.get(this.metric), textX, 0, this.textWidth);
    }

    private void drawMetric(Number number, String unit) {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        String text = new DecimalFormat("0.#").format(number) + ' ' + unit;
        double x = this.width / 2;
        g.setFont(this.metricFont);
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.setFill(Palette.TEXT_BLACK.color());
        g.fillText(text, x, this.metricY, this.width);
    }

    private void drawInoperative() {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        double x = this.width / 2;
        g.setFont(metricFont);
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.setFill(Palette.TEXT_BLACK.color());
        g.fillText("N/A", x, this.metricY, this.width);
    }

    private void drawBattery(double used, double total) {
        GraphicsContext g = super.canvas.getGraphicsContext2D();
        double margin = super.scheduler.getConfig().getPowerThreshold();
        double power = 1 - (used / total);
        double barWidth = this.width * power;
        double barHeight = this.width / 4;
        double y = this.metricY - barHeight / 2;
        String text = String.format("%.0f%%", power * 100);
        Color barColor;

        if (power < 0.2) {
            barColor = Color.ORANGERED;
        } else if (power < margin) {
            barColor = Color.YELLOW;
        } else {
            barColor = Color.LIGHTGREEN;
        }
        
//        g.setFill(Palette.COMPONENT_BACKGROUND.color());
//        g.fillRoundRect(0, this.metricY, this.width, this.height, this.strokeArc, this.strokeArc);

        g.setFill(barColor);
        g.fillRoundRect(0, y, barWidth, barHeight, this.strokeArc, this.strokeArc);
        
        g.setFill(Palette.STROKE_LIGHT.color());
        g.setLineWidth(2);
        g.strokeRoundRect(1, y + 1, this.width - 2, barHeight - 4, this.strokeArc, this.strokeArc);

        g.setFont(this.metricFont);
        g.setFill(Palette.TEXT_BLACK.color());
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.fillText(text, this.width / 2, this.metricY, this.width - 10);
    }
}
