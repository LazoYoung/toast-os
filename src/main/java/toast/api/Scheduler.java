package toast.api;

import java.util.List;
import java.util.Optional;
import java.util.Queue;

public interface Scheduler {

    /**
     * Returns a ready queue sorted by arrival time of each process. <br>
     * @return {@link List} of every process waiting to be dispatched
     */
    Queue<Process> getReadyQueue();

    /**
     * Returns a processor having no process running.
     * @return an {@link Optional} wrapping a {@link Processor}. If optional is empty, you can assume every processor is busy
     */
    Optional<Processor> getIdleProcessor();

    /**
     * @return a {@link List} of all processors
     */
    List<Processor> getProcessorList();

    /**
     * Tells how many seconds have passed after this Scheduler started.
     * @return number of seconds
     */
    int getElapsedTime();

}
