package toast.impl;

import toast.api.Process;

import java.util.ArrayList;
import java.util.List;

public class ToastProcess implements Process {
    private static int nextId = 0;

    private final List<Runnable> completionListeners = new ArrayList<>();
    private final int pid;
    private final int arrival;
    private final int workload;
    private int progress;
    private int burstTime;
    private int waitingTime;
    private int continuousBurstTime;

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
    public boolean isMission() {
        return false;
    }

    @Override
    public int addCompletionListener(Runnable listener) {
        completionListeners.add(listener);

        return completionListeners.size() - 1;
    }

    @Override
    public void removeCompletionListener(int listenerId) {
        completionListeners.remove(listenerId);

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ToastProcess other) {
            return (this.pid == other.pid);
        }
        return false;
    }

    public void standby() {
        waitingTime++;
    }

    public void work(int amount) {
        progress += amount;
        burstTime++;
        ++continuousBurstTime;

        if (isComplete()) {
            List<Runnable> listeners = new ArrayList<>(completionListeners);
            listeners.forEach(Runnable::run);
        }
    }

    public int getContinuousBurstTime() {
        return continuousBurstTime;
    }
    private boolean isComplete() {
        return progress >= workload;
    }

    public void halt() {
        continuousBurstTime = 0;
    }
}
