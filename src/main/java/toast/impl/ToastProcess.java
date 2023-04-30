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
    private final boolean isMission;
    private int progress = 0;
    private int burstTime = 0;
    private int waitingTime = 0;
    private int continuousBurstTime = 0;

    public ToastProcess(int arrival, int workload, boolean isMission) {
        this.pid = nextId++;
        this.arrival = arrival;
        this.workload = workload;
        this.isMission = isMission;
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
        return isMission;
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
        continuousBurstTime++;

        if (isComplete()) {
            List<Runnable> listeners = new ArrayList<>(completionListeners);
            listeners.forEach(Runnable::run);
        }
    }

    public int getContinuousBurstTime() {
        return continuousBurstTime;
    }

    public void halt() {
        continuousBurstTime = 0;
    }

    private boolean isComplete() {
        return progress >= workload;
    }
}
