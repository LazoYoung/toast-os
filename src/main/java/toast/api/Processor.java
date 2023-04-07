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
     * Tells how much power has this processor consumed
     *
     * @return number of watt
     */
    double getPowerConsumed();

    /**
     * Dispatch a process to this processor. <br>
     * If processor is not idle, make sure to {@link Processor#preempt()} first! <br>
     * Please ensure that you remove the process from ready queue. Refer to {@link Scheduler#getReadyQueue()}
     * @param process The {@link Process} you want to dispatch
     * @throws IllegalStateException thrown if processor is already running
     * @throws IllegalArgumentException thrown if process implementation is incompatible
     */
    void dispatch(Process process);

    /**
     * Halts the running process. <br>
     * Eventually this processor becomes idle. <br>
     * If there were no process running, this method does nothing.
     * @return The halted process
     */
    Process preempt();

}
