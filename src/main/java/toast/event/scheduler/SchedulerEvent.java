package toast.event.scheduler;

import toast.api.Scheduler;
import toast.event.ToastEvent;

public class SchedulerEvent extends ToastEvent {
    private final Scheduler scheduler;

    public SchedulerEvent(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}