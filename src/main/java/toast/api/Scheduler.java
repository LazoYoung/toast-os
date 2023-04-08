package toast.api;

import java.util.List;
import java.util.Optional;

public interface Scheduler {

    /**
     * Returns a ready queue sorted by arrival time of each process. <br>
     * Modifying this queue will throw {@link UnsupportedOperationException}. <br>
     * 프로세스가 도착한 순서대로 정렬된 대기열을 반환한다
     * @return {@link List} of every process waiting to be dispatched
     */
    List<Process> getReadyQueue();

    /**
     * Returns a processor having no process running. <br>
     * 할당된 프로세스가 없는 프로세서를 반환한다
     * @return an {@link Optional} wrapping a {@link Processor}. If optional is empty, you can assume every processor is busy
     */
    Optional<Processor> getIdleProcessor();

    /**
     * @return a {@link List} of all processors
     */
    List<Processor> getProcessorList();

    /**
     * Tells how many seconds have passed after this Scheduler started. <br>
     * 스케줄러가 시작된 후 몇 초 지났는지 알려준다
     * @return number of seconds
     */
    int getElapsedTime();

    /**
     * Dispatch the process to run. <br>
     * Upon success, the process gets removed from the queue. <br>
     * 대기열에 있는 프로세스를 프로세서에 할당한다. 성공한다면 해당 프로세스는 대기열에서 제거된다
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
     * and the existing process goes back to the end of queue. <br>
     * 대기열에 있는 프로세스를 프로세서에 할당한다. 성공한다면 해당 프로세스는 대기열에서 제거되고,
     * 이미 실행중이던 프로세스가 존재하면 선점 처리되어 중단되고 대기열 끝으로 돌아간다
     * @param processor The process that runs this process
     * @param process The process you want to dispatch
     * @throws IllegalStateException process is not in ready queue
     * @throws IllegalArgumentException processor is already running
     * @throws IllegalArgumentException process or processor implementation is incompatible
     */
    void preempt(Processor processor, Process process);

    /**
     * Finish the process scheduling simulation. <br>
     * 프로세스 스케줄링 시뮬레이터를 종료한다
     */
    void finish();

}
