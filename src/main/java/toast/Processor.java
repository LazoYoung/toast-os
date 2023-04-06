package toast;

import java.util.Optional;

public interface Processor {

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
     * Dispatch a process to this processor. The process becomes running state.
     * @param process The {@link Process} to be dispatched
     */
    void dispatch(Process process);

    /**
     * Halts the running process, adding it back to the ready queue.
     */
    void preempt();

}
