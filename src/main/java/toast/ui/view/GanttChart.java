package toast.ui.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import toast.ui.controller.GanttChartController;

public class GanttChart extends Pane {

    private final GanttChartController controller;

    public GanttChart() {
        Canvas canvas = createCanvas();
        controller = new GanttChartController(this, canvas);
    }

    @Override
    public void setWidth(double width) {
        super.setWidth(width);
        controller.resize(width, getPrefHeight());
    }

    @Override
    public void setHeight(double height) {
        super.setHeight(height);
        controller.resize(getPrefWidth(), height);
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
