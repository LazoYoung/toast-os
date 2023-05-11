package toast.event.scheduler;

import toast.api.Scheduler;

public class SchedulerStartEvent extends SchedulerEvent {
    public SchedulerStartEvent(Scheduler scheduler) {
        super(scheduler);
    }
}
