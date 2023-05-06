package toast.event.scheduler;

public class SchedulerFinishEvent extends SchedulerEvent {
    public enum Cause { COMPLETE, POWER_LOSS, ERROR }

    private final Cause cause;
    public SchedulerFinishEvent(Cause cause) {
        this.cause = cause;
    }

    public Cause getCause() {
        return cause;
    }
}
