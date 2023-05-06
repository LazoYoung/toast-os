package toast.event.process;

import toast.api.Process;
import toast.api.Processor;

public class ProcessDispatchEvent extends ProcessEvent {
    private final Processor processor;

    public ProcessDispatchEvent(Process process, int time, Processor processor) {
        super(process, time);
        this.processor = processor;
    }

    public Processor getProcessor() {
        return processor;
    }
}
