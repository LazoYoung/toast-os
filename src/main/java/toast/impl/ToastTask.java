package toast.impl;

import toast.algorithm.Algorithm;

import java.util.TimerTask;

public class ToastTask extends TimerTask {
    private final ToastScheduler scheduler;
    private final Algorithm algorithm;
    private int elapsedTime = 0;

    public ToastTask(ToastScheduler scheduler, Algorithm algorithm) {
        this.scheduler = scheduler;
        this.algorithm = algorithm;
    }

    @Override
    public void run() {
        algorithm.run(scheduler);
        elapsedTime++;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }
}
