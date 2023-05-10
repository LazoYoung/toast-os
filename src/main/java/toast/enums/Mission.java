package toast.enums;

import java.util.Arrays;

public enum Mission {
    F(false), T(true),
    ;
    private final Boolean value;

    public Boolean getValue() {
        return value;
    }

    Mission(Boolean value) {
        this.value = value;
    }

    public static Mission mappingFor(String missionName) {
        return Arrays.stream(values())
                .filter(mission -> mission.name().equals(missionName))
                .findAny()
                .orElseThrow(() -> new RuntimeException("올바르지 않은 미션입니다."));
    }
}
