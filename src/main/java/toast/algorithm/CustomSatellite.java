package toast.algorithm;

import toast.api.Process;
import toast.api.*;
import toast.event.ToastEvent;
import toast.event.process.ProcessCompleteEvent;
import toast.event.process.ProcessPreemptEvent;
import toast.event.processor.ProcessorDeactivateEvent;
import toast.event.scheduler.SchedulerFinishEvent;

import java.util.*;

public class CustomSatellite implements Algorithm {

    private Deque<Process> missionQueue;
    private Deque<Process> standardQueue;
    private final double initPower;
    private final double powerMargin;
    private final int timeQuantum;
    private Scheduler scheduler;
    private Processor missionCore;
    private Processor monoCore;
    private boolean powerMode;
    private boolean singleCoreMode;
    private boolean singleCoreFlag;
    private int singleCoreTimer;

    public CustomSatellite(int timeQuantum, double initPower, double powerThreshold) {
        if (initPower < 0)
            throw new IllegalArgumentException("Invalid range of initPower!");
        if (powerThreshold < 0 || powerThreshold > 1)
            throw new IllegalArgumentException("Invalid range of powerThreshold!");

        this.timeQuantum = timeQuantum;
        this.initPower = initPower;
        this.powerMargin = powerThreshold * initPower;
    }

    @Override
    public void init(Scheduler scheduler) {
        List<Processor> pList = scheduler.getActiveProcessorList();

        if (pList.isEmpty()) {
            throw new RuntimeException("Failed to initialize: no processor available");
        }

        this.scheduler = scheduler;
        this.missionQueue = new ArrayDeque<>();
        this.standardQueue = new ArrayDeque<>();
        this.singleCoreTimer = 0;
        this.singleCoreFlag = true;
        this.singleCoreMode = (pList.size() == 1);
        this.powerMode = false;
        this.monoCore = null;
        this.missionCore = this.singleCoreMode ? pList.get(0) : pList.stream()
                .filter(e -> e.getCore().equals(Core.PERFORMANCE))
                .findAny()
                .orElse(pList.get(0));
    }

    @Override
    public void run(Scheduler scheduler) {
        if (getAvailablePower() <= 0) {
            scheduler.finish(SchedulerFinishEvent.Cause.POWER_LOSS);
            return;
        }

        if (this.singleCoreMode) {
            runSingleCore();
        } else {
            runMultiCore();
        }
    }

    @Override
    public Iterator<Process> getStandardQueue() {
        return standardQueue.iterator();
    }

    @Override
    public Iterator<Process> getMissionQueue() {
        return missionQueue.iterator();
    }

    @Override
    public void onProcessReady(Process process) {
        enqueueProcess(process);
        ToastEvent.registerListener(ProcessCompleteEvent.class, (ProcessCompleteEvent event) -> onProcessComplete(event));
    }

    private void onProcessComplete(ProcessCompleteEvent event) {
        Process process = event.getProcess();
        if (this.singleCoreMode) {
            this.singleCoreTimer += process.getContinuousBurstTime();
        }
    }

    private void runSingleCore() {
        Optional<Process> runningProcess = this.missionCore.getRunningProcess();

        if (runningProcess.isEmpty()) {
            boolean hasNext = hasNextProcess(this.singleCoreFlag);
            boolean hasOpposite = hasNextProcess(!this.singleCoreFlag);
            boolean isTimeout = isSingleCoreTimeout(null);
            Process process = null;

            if (!hasNext && hasOpposite) {
                hasNext = true;
                hasOpposite = false;
                this.singleCoreFlag = !this.singleCoreFlag;
                this.singleCoreTimer = 0;
            }

            if (isTimeout && hasOpposite) {
                process = pollNextProcess(!this.singleCoreFlag);
                this.singleCoreFlag = !this.singleCoreFlag;
                this.singleCoreTimer = 0;
            } else if (hasNext) {
                process = pollNextProcess(this.singleCoreFlag);
            }

            if (process != null) {
                dispatch(this.missionCore, process);
            }
        } else {
            boolean negatedFlag = !this.singleCoreFlag;

            if (isSingleCoreTimeout(runningProcess.get()) && hasNextProcess(negatedFlag)) {
                Process nextProcess = pollNextProcess(negatedFlag);
                Process preempted = preempt(missionCore, nextProcess);
                insertProcess(preempted);
                this.singleCoreFlag = negatedFlag;
            }
        }
    }

    private void runMultiCore() {
        if (!this.powerMode && isPowerLow()) {
            enterPowerMode();
        }

        for (Processor processor : this.scheduler.getIdleProcessorList()) {
            boolean mission = processor.equals(missionCore);

            if (hasNextProcess(mission)) {
                Process process = pollNextProcess(mission);
                dispatch(processor, process);
            }
        }

        for (Processor processor : getTimeoutProcessors()) {
            assert (processor.getRunningProcess().isPresent());

            if (hasNextProcess(false)) {
                Process nextProcess = pollNextProcess(false);
                Process preempted = preempt(processor, nextProcess);
                this.standardQueue.addLast(preempted);
            }
        }
    }

    private void enterPowerMode() {
        List<Processor> standardCores = this.scheduler.getActiveProcessorList()
                .stream()
                .filter(p -> p != missionCore)
                .toList();
        this.monoCore = standardCores.stream()
                .filter(p -> p.getRunningProcess().isPresent())
                .max(getCoreAgeComparator())
                .orElse(standardCores.get(0));
        this.powerMode = true;

        standardCores.stream()
                .filter(p -> p != this.monoCore)
                .forEach(this::deactivateProcessor);

        System.out.println("│ Power-mode engaged");
    }

    private void deactivateProcessor(Processor processor) {
        if (processor.getRunningProcess().isPresent()) {
            Process halted = processor.halt();
            this.standardQueue.addLast(halted);
        }
        processor.deactivate(ProcessorDeactivateEvent.Cause.POWER_SAVING);
    }

    private boolean hasNextProcess(boolean mission) {
        return mission ? !this.missionQueue.isEmpty() : !this.standardQueue.isEmpty();
    }

    private Process pollNextProcess(boolean mission) {
        return mission ? this.missionQueue.pollFirst() : this.standardQueue.pollFirst();
    }

    private boolean isPowerLow() {
        return getAvailablePower() < this.powerMargin;
    }

    /**
     * @return List of standard Processors that used up all of their time quantum
     */
    private List<Processor> getTimeoutProcessors() {
        return this.scheduler.getActiveProcessorList()
                .stream()
                .filter(this::isTimeoutProcessor)
                .sorted(getCoreAgeComparator().reversed())
                .toList();
    }

    private Comparator<Processor> getCoreAgeComparator() {
        return (p1, p2) -> {
            if (p1.getRunningProcess().isEmpty())
                throw new IllegalArgumentException("Failed to compare processor time: p1 not running!");
            if (p2.getRunningProcess().isEmpty())
                throw new IllegalArgumentException("Failed to compare processor time: p2 not running!");

            int bt1 = p1.getRunningProcess().get().getContinuousBurstTime();
            int bt2 = p2.getRunningProcess().get().getContinuousBurstTime();
            return bt1 - bt2;
        };
    }

    private double getAvailablePower() {
        return this.initPower - this.scheduler.getPowerConsumed();
    }

    private boolean isSingleCoreTimeout(Process process) {
        int burstTime = this.singleCoreTimer;

        if (process != null) {
            burstTime += process.getContinuousBurstTime();
        }

        return burstTime >= this.timeQuantum;
    }

    private boolean isTimeoutProcessor(Processor processor) {
        Optional<Process> process = processor.getRunningProcess();

        if (process.isEmpty() || process.get().isMission()) {
            return false;
        } else {
            int cpuTime = process.get().getContinuousBurstTime();
            return cpuTime >= this.timeQuantum;
        }
    }

    private void enqueueProcess(Process process) {
        if (process.isMission()) {
            this.missionQueue.addLast(process);
        } else {
            this.standardQueue.addLast(process);
        }
    }

    private void insertProcess(Process process) {
        if (process.isMission()) {
            this.missionQueue.addFirst(process);
        } else {
            this.standardQueue.addFirst(process);
        }
    }

    private void dispatch(Processor processor, Process process) {
        processor.dispatch(process);
    }

    private Process preempt(Processor processor, Process process) {
        if (processor.isIdle()) {
            throw new IllegalStateException("Failed to preempt: processor not running");
        }

        Process halted = processor.halt();
        this.singleCoreTimer += halted.getContinuousBurstTime();

        processor.dispatch(process);

        // dispatch event
        int time = this.scheduler.getElapsedTime();
        var event = new ProcessPreemptEvent(halted, time, process);
        ToastEvent.dispatch(event.getClass(), event);

        return halted;
    }
}
