package toast.event.scheduler;

import toast.api.Scheduler;

public class SchedulerFinishEvent extends SchedulerEvent {
    public enum Cause { COMPLETE, POWER_LOSS, COMMAND }

    private final Cause cause;
    public SchedulerFinishEvent(Scheduler scheduler, Cause cause) {
        super(scheduler);
        this.cause = cause;
    }

    public Cause getCause() {
        return cause;
    }
}
