package toast.api;

import java.util.Optional;

public interface Processor {

    /**
     * Tells if this processor remains idle at this moment.
     * @return false if a process is running
     */
    boolean isIdle();

    /**
     * @return the {@link Core} of this processor
     */
    Core getCore();

    /**
     * Returns the {@link Process} running in this processor.
     * @return an {@link Optional} wrapping a {@link Process}. If optional is empty, you can assume this processor is idle
     */
    Optional<Process> getRunningProcess();

    /**
     * Tells how much power this processor has consumed
     * @return number of watt
     */
    int getPowerConsumed();

    /**
     * Dispatch a process to this processor. <br>
     * If this processor is not idle, make sure to {@link Processor#preempt()} first!
     * @param process The {@link Process} you want to dispatch
     * @throws IllegalStateException thrown if processor is already running
     */
    void dispatch(Process process);

    /**
     * Halts the running process and push it back to the ready queue. <br>
     * As a result, this processor becomes idle.
     * @throws IllegalStateException thrown if processor is idle
     */
    void preempt();

}
