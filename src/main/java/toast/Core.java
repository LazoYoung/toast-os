package toast;

import java.util.Optional;

public enum Core implements Processor {
    EFFICIENCY(1, 1),
    PERFORMANCE(2, 3);

    private final int workPerSec;
    private final int wattPerSec;
    private ConcreteProcess process;
    private int runningTime = 0;

    Core(int workPerSec, int wattPerSec) {
        this.workPerSec = workPerSec;
        this.wattPerSec = wattPerSec;
    }

    public void run() {
        if (process != null) {
            process.work();
            runningTime++;
        }
    }

    @Override
    public void dispatch(Process process) {
        if (!(process instanceof ConcreteProcess)) {
            throw new IllegalArgumentException("Process is not compatible with this core!");
        }

        // todo method stub
    }

    @Override
    public void preempt() {
        if (process == null) {
            throw new IllegalStateException("Process is empty!");
        }

        process = null;
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

    public int getRunningTime() {
        return runningTime;
    }

    public int getWorkPerSec() {
        return workPerSec;
    }

    public int getWattPerSec() {
        return wattPerSec;
    }

    public boolean isIdle() {
        return process == null;
    }
}