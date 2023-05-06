package toast.event.process;

import toast.api.Process;

public class ProcessCompleteEvent extends ProcessEvent {
    public ProcessCompleteEvent(Process process, int time) {
        super(process, time);
    }
}
