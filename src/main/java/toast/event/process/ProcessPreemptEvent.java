package toast.event.process;

import toast.api.Process;

public class ProcessPreemptEvent extends ProcessEvent {
    private final Process nextProcess;

    public ProcessPreemptEvent(Process process, int time, Process nextProcess) {
        super(process, time);
        this.nextProcess = nextProcess;
    }

    public Process getNextProcess() {
        return nextProcess;
    }
}
