package toast.algorithm;

import toast.api.Core;
import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CustomAlgorithm implements Algorithm {

    private final Deque<Process> missionQueue = new LinkedList<>();
    private final Deque<Process> standardQueue = new LinkedList<>();
    private final double initPower;
    private final int timeQuantum;
    private Processor missionCore;
    private boolean singleCoreMode;
    private boolean singleCoreFlag;
    private int singleCoreTimer;

    public CustomAlgorithm(int timeQuantum, double initPower) {
        this.timeQuantum = timeQuantum;
        this.initPower = initPower;
    }

    @Override
    public void init(Scheduler scheduler) {
        List<Processor> pList = scheduler.getProcessorList();

        if (pList.isEmpty()) {
            throw new RuntimeException("Failed to initialize: no processor available");
        }

        this.singleCoreTimer = 0;
        this.singleCoreFlag = true;
        this.singleCoreMode = (pList.size() == 1);
        this.missionCore = this.singleCoreMode ? pList.get(0) : pList.stream()
                .filter(e -> e.getCore().equals(Core.PERFORMANCE))
                .findAny()
                .orElse(pList.get(0));
    }

    @Override
    public void run(Scheduler scheduler) {
        if (isBatteryDischarged(scheduler)) {
            scheduler.finish();
            System.out.println("│[CUSTOM] Battery discharged! Shutting down...");
        } else if (this.singleCoreMode) {
            runSingleCore(scheduler);
        } else {
            runMultiCore(scheduler);
        }
    }

    @Override
    public void onProcessReady(Process process) {
        enqueueProcess(process);
        process.addCompletionListener(() -> onProcessComplete(process));
    }

    private void onProcessComplete(Process process) {
        if (this.singleCoreMode) {
            this.singleCoreTimer += process.getContinuousBurstTime();
        }
        System.out.printf("│[CUSTOM] Process #%d completed%n", process.getId());
    }

    private void runSingleCore(Scheduler scheduler) {
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
                dispatch(scheduler, this.missionCore, process);
            }
        } else {
            Process process = runningProcess.get();
            boolean negatedFlag = !this.singleCoreFlag;

            if (isSingleCoreTimeout(process) && hasNextProcess(negatedFlag)) {
                Process nextProcess = pollNextProcess(negatedFlag);
                preempt(scheduler, missionCore, nextProcess);
                insertProcess(process);
                this.singleCoreFlag = negatedFlag;
            }
        }
    }

    private void runMultiCore(Scheduler scheduler) {
        for (Processor processor : scheduler.getIdleProcessorList()) {
            boolean mission = processor.equals(missionCore);

            if (hasNextProcess(mission)) {
                Process process = pollNextProcess(mission);
                dispatch(scheduler, processor, process);
            }
        }

        for (Processor processor : getTimeoutProcessors(scheduler)) {
            assert (processor.getRunningProcess().isPresent());

            if (hasNextProcess(false)) {
                Process process = processor.getRunningProcess().get();
                Process nextProcess = pollNextProcess(false);
                preempt(scheduler, processor, nextProcess);
                this.standardQueue.addLast(process);
            }
        }
    }

    private boolean hasNextProcess(boolean mission) {
        return mission ? !this.missionQueue.isEmpty() : !this.standardQueue.isEmpty();
    }

    private Process pollNextProcess(boolean mission) {
        return mission ? this.missionQueue.pollFirst() : this.standardQueue.pollFirst();
    }

    /**
     * @return List of standard Processors that used up all of their time quantum
     */
    private List<Processor> getTimeoutProcessors(Scheduler scheduler) {
        return scheduler.getProcessorList()
                .stream()
                .filter(this::isTimeoutProcessor)
                .sorted(this::compareProcessorTime)
                .toList();
    }

    private boolean isBatteryDischarged(Scheduler s) {
        return s.getPowerConsumed() >= this.initPower;
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

    private int compareProcessorTime(Processor p1, Processor p2) {
        assert (p1.getRunningProcess().isPresent());
        assert (p2.getRunningProcess().isPresent());
        int bt1 = p1.getRunningProcess().get().getContinuousBurstTime();
        int bt2 = p2.getRunningProcess().get().getContinuousBurstTime();
        return bt2 - bt1;
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

    private void dispatch(Scheduler scheduler, Processor processor, Process process) {
        scheduler.dispatch(processor, process);

        int pid = process.getId();
        int cpuId = processor.getId();
        System.out.printf("│[CUSTOM] Dispatched process #%d to core #%d%n", pid, cpuId);
    }

    private void preempt(Scheduler scheduler, Processor processor, Process process) {
        if (processor.getRunningProcess().isEmpty()) {
            throw new IllegalStateException("Failed to preempt: processor not running");
        }

        Process preempted = processor.getRunningProcess().get();
        this.singleCoreTimer += preempted.getContinuousBurstTime();

        scheduler.preempt(processor, process);
        System.out.printf("│[CUSTOM] Process #%d preempted by #%d%n", preempted.getId(), process.getId());
    }
}
