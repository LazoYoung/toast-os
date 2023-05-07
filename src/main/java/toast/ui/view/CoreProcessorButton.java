package toast.ui.view;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.List;

public class CoreProcessorButton extends MFXButton {

    private int idx;

    private static List<CoreStatus> statuses = List.of(
            new CoreStatus("OFF", "-fx-background-color: #bbb4b4;"),
            new CoreStatus("P-Core", "-fx-background-color: #5a81fa;"),
            new CoreStatus("E-Core", "-fx-background-color: #2c3d8f;")
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
