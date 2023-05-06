package toast.event.processor;

import toast.api.Processor;
import toast.event.ToastEvent;

public class ProcessorEvent extends ToastEvent {
    private final Processor processor;
    private final int time;

    public ProcessorEvent(Processor processor, int time) {
        this.processor = processor;
        this.time = time;
    }

    public Processor getProcessor() {
        return processor;
    }

    public int getTime() {
        return time;
    }
}
