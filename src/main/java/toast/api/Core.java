package toast.api;

public enum Core {
    EFFICIENCY("E-CORE", 1, 1, 0.1),
    PERFORMANCE("P-CORE", 2, 3, 0.5);

    private final String name;
    private final int workload;
    private final int wattPerWork;
    private final double wattPerBoot;

    Core(String name, int workload, int wattPerWork, double wattPerBoot) {
        this.name = name;
        this.workload = workload;
        this.wattPerWork = wattPerWork;
        this.wattPerBoot = wattPerBoot;
    }

    public String getName() {
        return name;
    }

    public int getWorkload() {
        return workload;
    }

    public int getWattPerWork() {
        return wattPerWork;
    }

    public double getWattPerBoot() {
        return wattPerBoot;
    }

    public static Core mappingFor(int idx) {
        if (idx == 0) {
            return PERFORMANCE;
        }
        return idx == 1 ? PERFORMANCE : EFFICIENCY;
    }

    public int getIdx() {
        if (EFFICIENCY.equals(this)) {
            return 1;
        } else if (PERFORMANCE.equals(this)) {
            return 2;
        }

        throw new RuntimeException("해당하는 코어가 없음");
    }
}
