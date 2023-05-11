package toast.api;

import toast.event.processor.ProcessorDeactivateEvent;

import java.util.Optional;

public interface Processor {

    /**
     * 현재 프로세서에 할당된 프로세스가 없으면 idle 상태이다
     * @return false if this processor is not running
     */
    boolean isIdle();

    /**
     * 현재 프로세서에 할당된 프로세서가 있는지 여부를 반환한다
     * @return false if this processor is running
     */
    boolean isRunning();

    /**
     * Tells if this processor is currently available. <br>
     * 현재 프로세서가 사용 가능한지 반환한다
     * @return false if this processor is deactivated
     */
    boolean isActive();

    /**
     * 프로세서 고유 아이디를 반환한다
     * @return the unique id of this processor
     */
    int getId();

    /**
     * 프로세서 코어 타입을 반환한다
     * @return the {@link Core} of this processor
     */
    Core getCore();

    /**
     * Returns the {@link Process} running in this processor. <br>
     * 현재 프로세서가 실행중인 프로세스를 반환한다
     * @return an {@link Optional} wrapping a {@link Process}. If optional is empty, you can assume this processor is idle
     */
    Optional<Process> getRunningProcess();

    /**
     * Tells how much power has this processor consumed. <br>
     * 지금까지 소비한 전력량을 반환한다
     * @return number of watt
     */
    double getPowerConsumed();

    /**
     * Dispatch a process to this processor. <br>
     * 처리할 프로세스를 등록한다. 프로세서가 이미 실행중일 경우 {@link IllegalStateException} 발생.
     * @param process The {@link Process} you want to dispatch
     * @throws IllegalStateException processor is already running
     * @throws IllegalArgumentException process implementation is incompatible
     */
    void dispatch(Process process);

    /**
     * Halts the process and this processor becomes idle. <br>
     * If there were no process running, then it has no effect. <br>
     * 현재 실행중인 프로세스를 종료하고 반환한다.
     * @return The halted process
     */
    Process halt();

    /**
     * Deactivate this processor. <br>
     * 프로세스를 비활성화한다.
     * @param cause Cause of this event
     */
    void deactivate(ProcessorDeactivateEvent.Cause cause);

}
