package toast;

import java.util.List;
import java.util.Optional;

public interface Scheduler {

    /**
     * Tells how many seconds have passed after this Scheduler started.
     * @return number of seconds
     */
    int getElapsedTime();

    /**
     * Returns a processor that has no process running in the background.
     * @return an {@link Optional} wrapping a {@link Processor}. If optional is empty, you can assume every processor is busy
     */
    Optional<Processor> getIdleProcessor();

    /**
     * Returns all processors.
     * @return {@link List} of all cores
     */
    List<Processor> getProcessorList();

}
