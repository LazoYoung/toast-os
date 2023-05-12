package toast.impl;

import toast.api.Core;
import toast.api.Process;
import toast.api.Processor;
import toast.event.ToastEvent;
import toast.event.process.ProcessCompleteEvent;
import toast.event.process.ProcessDispatchEvent;
import toast.event.processor.ProcessorDeactivateEvent;
import toast.event.processor.ProcessorRebootEvent;

import java.util.Optional;

public class ToastProcessor implements Processor, Cloneable {
    private final int id;
    private final Core core;
    private ToastProcess process;
    private double powerConsumed = 0;
    private boolean bootRequired = true;
    private boolean active;
    private int currentTime = 0;
    private int completionListenerIdx;

    public ToastProcessor(int id, Core core, boolean active) {
        this.id = id;
        this.core = core;
        this.active = active;
    }

    @Override
    public void dispatch(Process process) {
        if (!(process instanceof ToastProcess)) {
            throw new IllegalArgumentException("Failed to dispatch: incompatible process");
        }
        if (this.process != null) {
            throw new IllegalStateException("Failed to dispatch: processor is already running");
        }
        if (!this.active) {
            throw new IllegalStateException("Failed to dispatch: processor is deactivated");
        }

        this.process = (ToastProcess) process;
        this.completionListenerIdx = ToastEvent.registerListener(ProcessCompleteEvent.class, (ProcessCompleteEvent event) -> {
            if (this.process.equals(event.getProcess())) {
                halt();
            }
        });

        this.process.assign(this);

        var event = new ProcessDispatchEvent(process, this.currentTime, this);
        ToastEvent.dispatch(event.getClass(), event);
    }

    @Override
    public Process halt() {
        if (this.process == null) return null;

        ToastProcess halted = this.process;
        this.process = null;
        halted.halt();
        ToastEvent.unregisterListener(this.completionListenerIdx);
        return halted;
    }

    @Override
    public void deactivate(ProcessorDeactivateEvent.Cause cause) {
        active = false;

        var event = new ProcessorDeactivateEvent(this, this.currentTime, cause);
        ToastEvent.dispatch(event.getClass(), event);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isIdle() {
        return process == null;
    }

    @Override
    public boolean isRunning() {
        return process != null;
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
     * @return amount of power drained
     */
    public double run() {
        if (process == null) {
            this.bootRequired = true;
            return 0;
        }

        int workload = core.getWorkload();
        double power = core.getWattPerWork();

        if (this.bootRequired) {
            power += core.getWattPerBoot();

            var event = new ProcessorRebootEvent(this, this.currentTime);
            ToastEvent.dispatch(event.getClass(), event);
        }

        this.powerConsumed += power;
        this.bootRequired = false;

        process.work(workload);
        return power;
    }

    /**
     * Clone this instance
     * @return a deep copied clone of this object
     * @throws IllegalStateException thrown if this processor is running
     */
    @Override
    public ToastProcessor clone() {
        if (this.process != null) {
            throw new IllegalStateException("Unable to clone a running processor!");
        }

        try {
            return (ToastProcessor) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
