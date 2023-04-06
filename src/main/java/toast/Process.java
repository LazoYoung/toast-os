package toast;

public class Process {
    private static int nextId = 0;
    private final int pid;
    private final int arrival;
    private final int workload;

    public Process(int arrival, int workload) {
        this.pid = nextId++;
        this.arrival = arrival;
        this.workload = workload;
    }

    public int getProcessId() {
        return pid;
    }

    public int getArrivalTime() {
        return arrival;
    }

    public int getBurstTime(boolean pCore) {
        return pCore ? (int) Math.ceil(workload / 2.0) : workload;
    }
}
