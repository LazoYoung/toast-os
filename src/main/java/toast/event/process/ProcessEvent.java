package toast.event.process;

import toast.api.Process;
import toast.event.ToastEvent;

public class ProcessEvent extends ToastEvent {
    private final Process process;
    private final int time;

    public ProcessEvent(Process process, int time) {
        this.process = process;
        this.time = time;
    }

    public Process getProcess() {
        return process;
    }

    public int getTime() {
        return time;
    }
}
