package toast.impl;

import toast.api.Process;
import toast.event.ToastEvent;
import toast.event.process.ProcessCompleteEvent;

@SuppressWarnings("removal")
public class ToastProcess implements Process {
    private static int nextId = 1;

    private final int id;
    private final int arrival;
    private final int workload;
    private final boolean isMission;
    private ToastProcessor processor = null;
    private int progress = 0;
    private int burstTime = 0;
    private int continuousBurstTime = 0;
    private int waitingTime = 0;
    private int lastHaltTime;

    public ToastProcess(int pId, int arrival, int workload, boolean isMission) {
        this.id = pId;
        this.arrival = arrival;
        this.workload = workload;
        this.isMission = isMission;
        this.lastHaltTime = arrival;
    }

    public ToastProcess(int arrival, int workload, boolean isMission) {
        this(nextId++, arrival, workload, isMission);
    }

    @Override
    public int getId() {
        return id;
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
    public int getContinuousBurstTime() {
        return continuousBurstTime;
    }

    @Override
    public boolean isMission() {
        return isMission;
    }

    @Override
    public int addCompletionListener(Runnable listener) {
        return 0;
    }

    @Override
    public void removeCompletionListener(int listenerId) {}

    @Override
    public boolean isRunning() {
        return (processor != null);
    }

    @Override
    public boolean isComplete() {
        return progress >= workload;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ToastProcess other) {
            return (this.id == other.id);
        }
        return false;
    }

    public void assign(ToastProcessor processor) {
        this.processor = processor;
        this.waitingTime += (processor.getCurrentTime() - this.lastHaltTime);
    }

    public void work(int workload) {
        this.progress += workload;
        this.burstTime++;
        this.continuousBurstTime++;

        if (isComplete()) {
            var event = new ProcessCompleteEvent(this, this.processor.getCurrentTime(), processor);
            ToastEvent.dispatch(ProcessCompleteEvent.class, event);
        }
    }

    public void halt() {
        if (!isRunning()) {
            throw new IllegalStateException("Failed to halt: process not running!");
        }

        this.lastHaltTime = this.processor.getCurrentTime();
        this.processor = null;
        this.continuousBurstTime = 0;
    }
}
