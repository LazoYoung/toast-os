package toast.impl;

import toast.api.Core;
import toast.api.Process;
import toast.api.Processor;

import java.util.Optional;

public class ToastProcessor implements Processor {
    private final Core core;
    private ToastProcess process;
    private double powerConsumed = 0;

    public ToastProcessor(Core core) {
        this.core = core;
    }

    @Override
    public Integer dispatch(Process process) {
        if (!(process instanceof ToastProcess)) {
            throw new IllegalArgumentException("Failed to dispatch: incompatible process");
        }
        if (this.process != null) {
            throw new IllegalStateException("Failed to dispatch: processor is already running");
        }

        this.powerConsumed += core.getWattPerBoot();
        this.process = (ToastProcess) process;

        return this.process.addCompletionListener(this::halt);

    }

    @Override
    public Process halt() {
        if (process == null) return null;

        Process halted = process;
        halted.halt();
        process = null;

        return halted;
    }

    @Override
    public boolean isIdle() {
        return process == null;
    }

    @Override
    public Optional<Process> getRunningProcess() {
        return Optional.ofNullable(process);
    }

    @Override
    public double getPowerConsumed() {
        return powerConsumed;
    }

    @Override
    public Core getCore() {
        return core;
    }

    public void run() {
        if (process == null) return;

        process.work(core.getWorkload());
        powerConsumed += core.getWattPerWork();
    }
}
