package toast.impl;

import toast.api.Process;

public class ToastProcess implements Process {
    private static int nextId = 0;

    private final int pid;
    private final int arrival;
    private final int workload;
    private int progress;
    private int burstTime;
    private int waitingTime;

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
        return waitingTime;
    }

    @Override
    public int getTurnaroundTime() {
        if (!isComplete()) {
            throw new IllegalStateException("Process not complete!");
        }

        return waitingTime + burstTime;
    }

    @Override
    public double getNormalizedTurnaroundTime() {
        return (double) getTurnaroundTime() / burstTime;
    }

    @Override
    public int getWorkload() {
        return workload;
    }

    @Override
    public int getRemainingWorkload() {
        return workload - progress;
    }

    public boolean isComplete() {
        return progress >= workload;
    }

    public void standby() {
        waitingTime++;
    }

    public void work(int amount) {
        progress += amount;
        burstTime++;
    }
}
