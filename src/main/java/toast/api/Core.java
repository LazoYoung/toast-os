package toast.api;

import java.util.Optional;

public enum Core {
    EFFICIENCY(1, 1),
    PERFORMANCE(2, 3);

    private final int workPerSec;
    private final int wattPerSec;

    Core(int workPerSec, int wattPerSec) {
        this.workPerSec = workPerSec;
        this.wattPerSec = wattPerSec;
    }


    public int getWorkPerSec() {
        return workPerSec;
    }

    public int getWattPerSec() {
        return wattPerSec;
    }

}