package toast;

import java.util.List;
import java.util.Timer;

public class Scheduler {
    private final Timer timer = new Timer();
    private SchedulerTask task = null;
    private boolean started = false;

    public void start(Algorithm algorithm, List<ConcreteProcess> processList) {
        if (started) {
            throw new IllegalStateException("Scheduler already started.");
        }

        task = new SchedulerTask(algorithm);
        started = true;
        timer.scheduleAtFixedRate(task, 0L, 1000L);
    }

    public void stop() {
        if (!started) {
            throw new IllegalStateException("Scheduler not started yet.");
        }

        started = false;
        task.cancel();
    }

    public int getElapsedTime() {
        return (task != null) ? task.getElapsedTime() : 0;
    }
}
