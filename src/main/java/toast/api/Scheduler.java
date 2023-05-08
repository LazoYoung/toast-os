package toast.api;

import toast.event.scheduler.SchedulerFinishEvent;

import java.util.List;

public interface Scheduler {

    /**
     * Returns a ready queue sorted by arrival time of each process. <br>
     * Modifying this queue will throw {@link UnsupportedOperationException}. <br>
     * 프로세스가 도착한 순서대로 정렬된 대기열을 반환한다
     * @deprecated Replaced by {@link toast.api.Algorithm#onProcessReady(Process)} since issue-32
     * @return {@link List} of every {@link Process} waiting to be dispatched
     */
    @Deprecated(forRemoval = true)
    List<Process> getReadyQueue();

    /**
     * Returns every processor. <br>
     * 모든 프로세서 목록을 반환한다
     * @return a {@link List} of all processors
     */
    List<Processor> getProcessorList();

    /**
     * Returns processors having no process running. <br>
     * 할당된 프로세스가 없는 프로세서 목록을 반환한다
     * @return {@link List} of every idle {@link Processor}. If it's empty, you can assume every processor is busy
     */
    List<Processor> getIdleProcessorList();

    /**
     * Returns available processors. <br>
     * 사용 가능한 프로세서 목록을 반환한다
     * @return a {@link List} of available processors
     */
    List<Processor> getActiveProcessorList();

    /**
     * Returns every processes. <br>
     * 프로세스 목록을 반환한다
     * @return a {@link List} of all processes
     */
    List<Process> getProcessList();

    /**
     * Tells how many seconds have passed after this Scheduler started. <br>
     * 스케줄러가 시작된 후 몇 초 지났는지 알려준다
     * @return number of seconds
     */
    int getElapsedTime();

    /**
     * 지금까지 소비한 총 전력량을 반환한다
     * @return the amount of electrical power that entire system has used until now
     */
    double getPowerConsumed();

    /**
     * 모든 프로세스의 평균 턴어라운드 시간을 반환한다
     * @return the average turnaround time for every process in seconds
     */
    double getAverageTT();

    /**
     * Dispatch the process to run. <br>
     * Upon success, the process gets removed from the queue. <br>
     * 대기열에 있는 프로세스를 프로세서에 할당한다. 성공한다면 해당 프로세스는 대기열에서 제거된다
     * @deprecated Replaced by {@link Processor#dispatch(Process)} since issue-32
     * @param processor The processor that runs this process
     * @param process The process you want to dispatch
     * @throws IllegalStateException process is not in ready queue
     * @throws IllegalArgumentException processor is already running
     * @throws IllegalArgumentException process or processor implementation is incompatible
     */
    @Deprecated(forRemoval = true)
    void dispatch(Processor processor, Process process);

    /**
     * Preempt the process to run. <br>
     * Upon success, the process gets removed from the queue
     * and the existing process goes back to the end of queue. <br>
     * 대기열에 있는 프로세스를 프로세서에 할당한다. 성공한다면 해당 프로세스는 대기열에서 제거되고,
     * 이미 실행중이던 프로세스가 존재하면 선점 처리되어 중단되고 대기열 끝으로 돌아간다
     * @deprecated Deprecated since issue-32
     * @param processor The process that runs this process
     * @param process The process you want to dispatch
     * @throws IllegalStateException process is not in ready queue
     * @throws IllegalArgumentException processor is already running
     * @throws IllegalArgumentException process or processor implementation is incompatible
     */
    @Deprecated(forRemoval = true)
    void preempt(Processor processor, Process process);

    /**
     * Finish the process scheduling simulation. <br>
     * 프로세스 스케줄링 시뮬레이터를 종료한다
     * @param cause The cause of this event
     */
    void finish(SchedulerFinishEvent.Cause cause);

    /**
     * 실행중인 프로세스를 종료시키고 큐의 맨 뒤에 넣는다.
     *
     * @deprecated Replaced by {@link Processor#halt()} since issue-32
     * @param processor 종료시킬 프로세서
     */
    @Deprecated(forRemoval = true)
    void halt(Processor processor);
}
