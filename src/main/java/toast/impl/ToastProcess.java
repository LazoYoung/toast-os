package toast.impl;

import toast.api.Process;

import java.util.ArrayList;
import java.util.List;

public class ToastProcess implements Process {
    private static int nextId = 1;

    private final List<Runnable> completionListeners = new ArrayList<>();
    private final int pid;
    private final int arrival;
    private final int workload;
    private int progress = 0;
    private int burstTime = 0;
    private int waitingTime = 0;

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

    @Override
    public void addCompletionListener(Runnable listener) {
        completionListeners.add(listener);
    }

    public void standby() {
        waitingTime++;
    }

    public void work(int amount) {
        progress += amount;
        burstTime++;

        if (isComplete()) {
            completionListeners.forEach(Runnable::run);
            System.out.printf("│ Process #%d completed%n", pid);
        }
    }

    private boolean isComplete() {
        return progress >= workload;
    }
}
