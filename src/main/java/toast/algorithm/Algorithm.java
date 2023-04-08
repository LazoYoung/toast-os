package toast.algorithm;

import toast.api.Scheduler;

public interface Algorithm {

    /**
     * Write your scheduling algorithm here! <br>
     * In every second, this method is executed before each processor runs. <br>
     * Refer to {@link toast.impl.ToastScheduler} for implementation under the hood. <br>
     * 스케줄링을 처리하는 함수. 1초에 한 번 프로세서가 실행되기 직전에 실행된다.
     * @param scheduler Scheduler API
     */
    void run(Scheduler scheduler);

}
