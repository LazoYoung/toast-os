package toast.event.processor;

import toast.api.Processor;

public class ProcessorRebootEvent extends ProcessorEvent {
    public ProcessorRebootEvent(Processor processor, int time) {
        super(processor, time);
    }
}
