package toast.api;

public interface Process {
    /**
     * @return unique id of this process
     */
    int getId();

    /**
     * Indicates the amount of time that this process must wait
     * before it enters the ready queue. <br>
     * 이 프로세스가 대기열에 추가될 때까지 기다려야 하는 시간
     * @return arrival time in seconds
     */
    int getArrivalTime();

    /**
     * Indicates the amount of time that this process has been waiting
     * in the ready queue up to this point. <br>
     * 이 프로세스가 현재까지 대기열에서 얼마나 기다렸는지 알려준다
     * @return waiting time in seconds
     */
    int getWaitingTime();

    /**
     * TT = WT + BT
     * @return turnaround time in seconds
     */
    int getTurnaroundTime();

    /**
     * NTT = (WT + BT) / BT
     * @return norm. turnaround time in seconds
     */
    double getNormalizedTurnaroundTime();

    /**
     * Workload is defined as the amount of time
     * that an E-CORE needs to work to finish the process. <br>
     * E코어 기준으로 프로세스를 처리하는 데 필요한 시간을 작업량으로 정의한다
     * @return the total amount of workload
     */
    int getWorkload();

    /**
     * Indicates the amount of extra workload
     * that needs to be done to finish this process. <br>
     * 현재 프로세스의 남은 작업량
     * @return the amount of remaining workload
     */
    int getRemainingWorkload();

    /**
     * Registers a Process Completion event listener.
     *
     * @param listener Runnable to be called once the process finishes
     * @return listener id
     */
    Integer addCompletionListener(Runnable listener);

    /**
     * 프로세스를 정지함; 연속 실행 시간을 0으로 초기화함.
     */
    void halt();

    /**
     * 프로세스의 연속 실행 시간을 반환함.
     */
    int getContinuousBurstTime();

    /**
     * 선점시 제거해야 하는 리스너를 ID를 통해 제거한다.
     *
     * @param listenerId 종료할 리스너 이벤트의 아이디
     */
    void removeListener(int listenerId);
}
