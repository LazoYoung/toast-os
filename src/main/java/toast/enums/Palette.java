package toast.enums;

import javafx.scene.paint.Color;

public enum Palette {
    /**
     * 어두운 텍스트
     */
    TEXT_BLACK("#333F50"),

    /**
     * 밝은 텍스트
     */
    TEXT_WHITE("#FFFFFF"),

    /**
     * 비활성화된 텍스트
     */
    TEXT_DISABLED("#BFBFBF"),

    /**
     * 윤곽선
     */
    STROKE("#D9D9D9"),

    STROKE_LIGHT("#F2F2F2"),

    /**
     * 결과 페이지 소제목, 컴포넌트 부제목 등
     */
    TEXT_WIDGET("#8497B0"),

    /**
     * 컴포넌트 배경색
     */
    COMPONENT_BACKGROUND("#FFFFFF"),

    /**
     * 컴포넌트를 붙이는 전체화면 배경색
     */
    WINDOW_BACKGROUND("#F3F5F7"),

    /**
     * P-Core 색상
     */
    P_CORE("#5A81FA"),

    /**
     * E-Core 색상
     */
    E_CORE("#2C3D8F"),

    /**
     * 비활성화된 코어 색상
     */
    CORE_OFF("#F8F9FD"),
    ;

    private final String hexCode;

    Palette(String hexCode) {
        this.hexCode = hexCode;
    }

    public Color color() {
        return Color.web(hexCode);
    }

    public String hex() {
        return hexCode;
    }
}
