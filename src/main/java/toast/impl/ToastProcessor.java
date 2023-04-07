package toast.impl;

import toast.api.Core;
import toast.api.Process;
import toast.api.Processor;

import java.util.Optional;

public class ToastProcessor implements Processor {
    private final Core core;
    private ToastProcess process;

    public ToastProcessor(Core core) {
        this.core = core;
    }

    @Override
    public void dispatch(Process process) {
        if (!(process instanceof ToastProcess)) {
            throw new IllegalArgumentException("Failed to dispatch: incompatible process");
        }
        if (this.process != null) {
            throw new IllegalStateException("Failed to dispatch: processor is already running");
        }

        // todo method stub
    }

    @Override
    public void preempt() {
        if (process == null) {
            throw new IllegalStateException("Failed to preempt: processor is idle");
        }

        process = null;
    }

    @Override
    public boolean isIdle() {
        return process == null;
    }

    @Override
    public Optional<Process> getRunningProcess() {
        return Optional.of(process);
    }

    @Override
    public int getPowerConsumed() {
        // todo method stub
        return 0;
    }

    @Override
    public Core getCore() {
        return core;
    }

    public void run() {
        if (process != null) {
            process.work();
        }
    }
}
