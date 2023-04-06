package toast;

import java.util.List;

public abstract class Scheduler {
    protected List<Process> processList;

    public Scheduler(List<Process> processList) {
        this.processList = processList;
    }

    /**
     * Implement your scheduling algorithm here!
     * This method repeats every 1 second.
     * @return true if every process is completed
     */
    public abstract boolean run();

}
