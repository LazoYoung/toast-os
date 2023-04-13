package toast.api;

import java.util.Optional;

public interface Processor {

    /**
     * Tells if this processor remains idle at this moment. <br>
     * 현재 프로세서에 할당된 프로세스가 없으면 idle 상태이다
     * @return false if a process is running
     */
    boolean isIdle();

    /**
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
     * Please refrain from using this method as it's intended for internal use. <br>
     * There is an alternative: {@link Scheduler#dispatch(Processor, Process)} <br>
     * 처리할 프로세스를 선택하여 등록한다. 본 함수는 구현체 내부에서 사용되는 용도이므로 사용을 지양하시오
     * @param process The {@link Process} you want to dispatch
     * @return index of listener event (리스너가 추후 실행할 Runnable의 인덱스)
     * @throws IllegalStateException processor is already running
     * @throws IllegalArgumentException process implementation is incompatible
     */
    int dispatch(Process process);

    /**
     * Halts the process and this processor becomes idle. <br>
     * If there were no process running, then it has no effect. <br>
     * 현재 실행중인 프로세스를 종료한다
     * @return The halted process
     */
    Process halt();

}
