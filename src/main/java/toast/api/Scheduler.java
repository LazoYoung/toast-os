package toast.api;

import java.util.List;
import java.util.Optional;
import java.util.Queue;

public interface Scheduler {

    /**
     * Returns a ready queue sorted by arrival time of each process. <br>
     * Note that manipulating this queue has no effect in the system. <br>
     * You don't need to because {@link Scheduler#dispatch(Processor, Process)} and {@link Scheduler#preempt(Processor, Process)}
     * functions take care of the queue for you.
     * @return {@link Queue} of every process waiting to be dispatched
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
