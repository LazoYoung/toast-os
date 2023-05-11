package toast.api;

import java.util.Collections;
import java.util.Iterator;

public interface Algorithm {

    /**
     * Initialize your algorithm if required. <br>
     * 스케줄링 초기화 작업을 처리하는 함수
     * @param scheduler Scheduler API
     */
    void init(Scheduler scheduler);

    /**
     * Callback to be invoked when the scheduler introduces a new process to the ready queue. <br>
     * 새로운 프로세스가 ready 상태로 진입하면 실행되는 콜백 함수
     * @param process The process that is entering the ready state.
     */
    void onProcessReady(Process process);

    /**
     * Get access to the ready queue for standard processes. <br>
     * 표준 프로세스 대기열 Iterator 를 반환하는 함수
     * @return The {@link Iterator} backing the standard ready queue
     */
    default Iterator<Process> getStandardQueue() {
        return Collections.emptyIterator();
    }

    /**
     * Get access to the ready queue for mission processes. <br>
     * 미션 프로세스 대기열 Iterator 를 반환하는 함수
     * @return The {@link Iterator} backing the mission ready queue
     */
    default Iterator<Process> getMissionQueue() {
        return Collections.emptyIterator();
    }

    /**
     * Write your scheduling algorithm here! <br>
     * In every second, this method is executed before each processor runs. <br>
     * Refer to {@link toast.impl.ToastScheduler} for implementation under the hood. <br>
     * 스케줄링을 처리하는 함수. 1초에 한 번 프로세서가 실행되기 직전에 실행된다
     * @param scheduler Scheduler API
     */
    void run(Scheduler scheduler);

}
