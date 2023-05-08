package toast.ui.view;

import io.github.palexdev.materialfx.controls.MFXButton;

import java.util.List;

public class CoreProcessorButton extends MFXButton {

    private int idx;

    private static final List<CoreStatus> statuses = List.of(
            new CoreStatus("OFF", getStyle("#000000", "#bbb4b4")),
            new CoreStatus("P-Core", getStyle("#FFFFFF", "#5a81fa")),
            new CoreStatus("E-Core", getStyle("#FFFFFF", "#2c3d8f"))
    );

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
        updateStatus();
    }

    public CoreProcessorButton() {
        this.idx = 0;

        updateStatus();

        setOnMouseClicked(e -> {
            ++idx;
            idx %= statuses.size();
            updateStatus();
        });
    }

    private static String getStyle(String textColor, String bgColor) {
        String style = "-fx-text-fill: %s; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: %s; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.8),10,0,5,5);";
        return String.format(style, textColor, bgColor);
    }

    private void updateStatus() {
        setStyle(getCustomStyle());
        setText(getCustomText());
    }

    private String getCustomText() {
        return statuses.get(this.idx).text;
    }

    private String getCustomStyle() {
        return statuses.get(this.idx).style;
    }

    static class CoreStatus {
        public String text;
        public String style;

        public CoreStatus(String text, String style) {
            this.text = text;
            this.style = style;
        }
    }
}
