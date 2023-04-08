package toast.algorithm;

import toast.api.Scheduler;

public interface Algorithm {

    /**
     * Write your scheduling algorithm here! <br>
     * In every second, this method is executed before each processor runs. <br>
     * Refer to {@link toast.impl.ToastScheduler} for implementation under the hood.
     * @param scheduler Scheduler API
     */
    void run(Scheduler scheduler);

}
