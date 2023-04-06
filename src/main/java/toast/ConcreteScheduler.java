package toast;

import java.util.List;
import java.util.Optional;
import java.util.Timer;

public class ConcreteScheduler implements Scheduler {
    private final Algorithm algorithm;
    private final Timer timer = new Timer();
    private SchedulerTask task = null;
    private boolean started = false;

    public ConcreteScheduler(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void start(List<ConcreteProcess> processList) {
        if (started) {
            throw new IllegalStateException("Scheduler already started.");
        }

        task = new SchedulerTask(this);
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

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public int getElapsedTime() {
        return (task != null) ? task.getElapsedTime() : 0;
    }

    @Override
    public Optional<Processor> getIdleProcessor() {
        // todo method stub
        return Optional.empty();
    }

    @Override
    public List<Processor> getProcessorList() {
        // todo method stub
        return List.of();
    }
}
