package toast.impl;

import toast.api.Process;

public class ToastProcess implements Process {
    private static int nextId = 0;
    private final int pid;
    private final int arrival;
    private final int workload;
    private int counter;
    private int burstTime;

    public ToastProcess(int arrival, int workload) {
        this.pid = nextId++;
        this.arrival = arrival;
        this.workload = workload;
    }

    @Override
    public int getId() {
        return pid;
    }

    @Override
    public int getArrivalTime() {
        return arrival;
    }

    @Override
    public int getWaitingTime() {
        // todo method stub
        return 0;
    }

    @Override
    public int getTurnaroundTime() {
        // todo method stub
        return 0;
    }

    @Override
    public int getNormalizedTurnaroundTime() {
        // todo method stub
        return 0;
    }

    @Override
    public int getWorkload() {
        return workload;
    }

    @Override
    public int getRemainingWorkload() {
        return workload - counter;
    }

    public boolean isComplete() {
        return counter >= workload;
    }

    public void work() {
        counter++;
        burstTime++;
    }
}
