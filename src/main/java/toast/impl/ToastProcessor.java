package toast.impl;

import toast.api.Core;
import toast.api.Process;
import toast.api.Processor;

import java.util.Optional;

public class ToastProcessor implements Processor {
    private static int newId = 1;
    private final int id;
    private final Core core;
    private ToastProcess process;
    private double powerConsumed = 0;
    private int currentTime = 0;
    private int completionListenerIdx;

    public ToastProcessor(Core core) {
        this.id = newId++;
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

        this.powerConsumed += this.core.getWattPerBoot();
        this.process = (ToastProcess) process;
        this.completionListenerIdx = this.process.addCompletionListener(this::halt);

        this.process.assign(this);
    }

    @Override
    public Process halt() {
        if (this.process == null) return null;

        ToastProcess halted = this.process;
        this.process = null;
        halted.halt();
        halted.removeCompletionListener(this.completionListenerIdx);
        return halted;
    }

    @Override
    public boolean isIdle() {
        return process == null;
    }

    @Override
    public int getId() {
        return id;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ToastProcessor other) {
            return (this.id == other.id);
        }
        return false;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void updateTime(int time) {
        this.currentTime = time;
    }

    /**
     * Sync current time and run the process.
     * @return amount of power drained
     */
    public double run() {
        if (process == null) return 0;

        double power = core.getWattPerWork();
        process.work(core.getWorkload());
        powerConsumed += power;
        return power;
    }
}
