package toast;

public abstract class Algorithm {
    private final Scheduler scheduler;

    public Algorithm(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Implement your scheduling algorithm here!
     * This method repeats every 1 second.
     */
    public abstract void run();

    public int getElapsedTime() {
        return scheduler.getElapsedTime();
    }
}
