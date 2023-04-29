package toast.api;

import java.util.List;

public interface Scheduler {

    /**
     * Returns a ready queue sorted by arrival time of each process. <br>
     * Modifying this queue will throw {@link UnsupportedOperationException}. <br>
     * 프로세스가 도착한 순서대로 정렬된 대기열을 반환한다
     * @return {@link List} of every {@link Process} waiting to be dispatched
     */
    List<Process> getReadyQueue();

    /**
     * Returns processors having no process running. <br>
     * 할당된 프로세스가 없는 프로세서 목록을 반환한다
     * @return {@link List} of every idle {@link Processor}. If it's empty, you can assume every processor is busy
     */
    List<Processor> getIdleProcessorList();

    /**
     * Returns every processor. <br>
     * 프로세서 목록을 반환한다
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
     * 지금까지 사용한 전력량을 반환한다
     * @return the amount of electrical power it has used until now
     */
    double getPowerConsumed();

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

    /**
     * 실행중인 프로세스를 종료시키고 큐의 맨 뒤에 넣는다.
     *
     * @param processor 종료시킬 프로세서
     */
    void halt(Processor processor);
}
