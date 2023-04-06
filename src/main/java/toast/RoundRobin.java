package toast;

public class RoundRobin extends Algorithm {
    public RoundRobin(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    public void run() {
        int time = getElapsedTime();
        System.out.printf("[RR] Processing... (%ds)%n", time);
    }
}
