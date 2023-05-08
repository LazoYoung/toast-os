package toast.event.process;

import toast.api.Process;
import toast.api.Processor;

public class ProcessCompleteEvent extends ProcessEvent {
    private final Processor processor;

    public ProcessCompleteEvent(Process process, int time, Processor processor) {
        super(process, time);
        this.processor = processor;
    }

    public Processor getLastProcessor() {
        return processor;
    }
}
