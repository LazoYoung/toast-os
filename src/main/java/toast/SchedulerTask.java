package toast;

import java.util.TimerTask;

public class SchedulerTask extends TimerTask {
    private final ConcreteScheduler scheduler;
    private final Algorithm algorithm;
    private int elapsedTime = 0;

    public SchedulerTask(ConcreteScheduler scheduler) {
        this.scheduler = scheduler;
        this.algorithm = scheduler.getAlgorithm();
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
