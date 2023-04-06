package toast;

import java.util.TimerTask;

public class SchedulerTask extends TimerTask {
    private final Algorithm algorithm;
    private int elapsedTime = 0;

    public SchedulerTask(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public void run() {
        algorithm.run();
        elapsedTime++;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }
}
