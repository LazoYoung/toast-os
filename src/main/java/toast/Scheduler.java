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

    /**
     * Retrieve a processor that has no process running in the background.
     * @return a sleeping core. (Expect null if every processor is busy)
     */
    public Processor getIdleProcessor() {
        // todo method stub
        return null;
    }

    /**
     * Retrieve all processors.
     * @return List of all cores
     */
    public List<Processor> getProcessorList() {
        // todo method stub
        return null;
    }
}
