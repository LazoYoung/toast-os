package toast.api;

import java.util.List;
import java.util.Optional;

public interface Scheduler {

    /**
     * Returns a ready queue sorted by arrival time of each process. <br>
     * Modifying this queue will throw {@link UnsupportedOperationException}.
     * @return {@link List} of every process waiting to be dispatched
     */
    List<Process> getReadyQueue();

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

    /**
     * Dispatch the process to run. <br>
     * Upon success, the process gets removed from the queue.
     * @param processor The processor that runs this process
     * @param process The process you want to dispatch
     * @throws IllegalStateException process is not in ready queue
     * @throws IllegalArgumentException processor is already running
     * @throws IllegalArgumentException process or processor implementation is incompatible
     */
    void dispatch(Processor processor, Process process);

    /**
     * Preempt the process to run. <br>
     * Upon success, the process gets removed from the queue
     * and the existing process goes back to the end of queue.
     * @param processor The process that runs this process
     * @param process The process you want to dispatch
     * @throws IllegalStateException process is not in ready queue
     * @throws IllegalArgumentException processor is already running
     * @throws IllegalArgumentException process or processor implementation is incompatible
     */
    void preempt(Processor processor, Process process);

    /**
     * Finish scheduling simulation.
     */
    void finish();

}
