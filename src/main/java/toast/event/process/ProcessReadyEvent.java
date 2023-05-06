package toast.event.process;

import toast.api.Process;

public class ProcessReadyEvent extends ProcessEvent {
    public ProcessReadyEvent(Process process, int time) {
        super(process, time);
    }
}
