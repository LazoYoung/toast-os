package toast;

public class ShortestProcessNext implements Algorithm {
    @Override
    public void run(Scheduler scheduler) {
        // todo method stub
        int time = scheduler.getElapsedTime();
        System.out.printf("[SPN] Running... (%ss)%n", time);
    }
}
