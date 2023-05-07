package toast.event.processor;

import toast.api.Processor;

public class ProcessorDeactivateEvent extends ProcessorEvent {
    public enum Cause { POWER_SAVING, POWER_LOSS, FINISH }

    private final Cause cause;

    public ProcessorDeactivateEvent(Processor processor, int time, Cause cause) {
        super(processor, time);
        this.cause = cause;
    }

    public Cause getCause() {
        return cause;
    }
}
