package toast;

import java.util.List;

public class RoundRobin extends Scheduler {
    private static int count = 0;

    public RoundRobin(List<Process> processList) {
        super(processList);
    }

    @Override
    public boolean run() {
        if (count > 3) {
            System.out.println("[RR] Complete!");
            return true;
        }

        System.out.printf("[RR] Processing... (%ds)%n", count++);
        return false;
    }
}
