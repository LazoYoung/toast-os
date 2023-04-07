package toast.api;

public enum Core {
    EFFICIENCY(1, 1, 0.1),
    PERFORMANCE(2, 3, 0.5);

    private final int workload;
    private final int wattPerWork;
    private final double wattPerBoot;

    Core(int workload, int wattPerWork, double wattPerBoot) {
        this.workload = workload;
        this.wattPerWork = wattPerWork;
        this.wattPerBoot = wattPerBoot;
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
}