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
    private int timer;

    public CustomAlgorithm(int timeQuantum, double initPower) {
        this.timeQuantum = timeQuantum;
        this.initPower = initPower;
    }

    @Override
    public void init(Scheduler scheduler) {
        List<Processor> pList = scheduler.getProcessorList();

        if (pList.isEmpty()) {
            throw new RuntimeException("Failed to initialize! No processor is available.");
        }

        this.timer = 0;
        this.singleCoreFlag = true;
        this.singleCoreMode = (pList.size() == 1);
        this.missionCore = this.singleCoreMode ? pList.get(0) : pList.stream()
                .filter(e -> e.getCore().equals(Core.PERFORMANCE))
                .findAny()
                .orElse(pList.get(0));
    }

    @Override
    public void run(Scheduler scheduler) {
        if (!isPowerAvailable(scheduler)) {
            scheduler.finish();
            System.out.println("│[CUSTOM] Battery discharged! Shutting down...");
            return;
        }

        if (this.singleCoreMode) {
            Optional<Process> missionProcess = this.missionCore.getRunningProcess();

            if (missionProcess.isEmpty()) {
                Optional<Process> nextProcess = getNextProcess(singleCoreFlag);

                if (nextProcess.isEmpty()) {
                    this.timer = 0;
                    this.singleCoreFlag = !this.singleCoreFlag;
                    nextProcess = getNextProcess(singleCoreFlag);
                }
                nextProcess.ifPresent(e -> dispatch(scheduler, this.missionCore, e));
            } else if (++this.timer >= this.timeQuantum) {
                Process process = missionProcess.get();
                getOppositeProcess(process).ifPresent(nextProcess -> {
                    scheduler.preempt(this.missionCore, nextProcess);
                    insertProcess(process);
                    this.timer = 0;
                });
            }
        } else {
            for (Processor processor : scheduler.getIdleProcessorList()) {
                boolean mission = processor.equals(missionCore);
                getNextProcess(mission).ifPresent(e -> dispatch(scheduler, processor, e));
            }
        }

        for (Processor processor : getTimeoutProcessors(scheduler)) {
            assert (processor.getRunningProcess().isPresent());
            if (this.standardQueue.isEmpty()) break;

            Process process = processor.getRunningProcess().get();
            Process nextProcess = this.standardQueue.removeFirst();
            scheduler.preempt(processor, nextProcess);
            this.standardQueue.addLast(process);
            System.out.printf("│[CUSTOM] Process #%d preempted by #%d%n", process.getId(), nextProcess.getId());
        }
    }

    @Override
    public void onProcessReady(Process process) {
        enqueueProcess(process);
        process.addCompletionListener(() -> onProcessComplete(process));
    }

    private void onProcessComplete(Process process) {
        if (this.singleCoreMode) {
            this.singleCoreFlag = !process.isMission();
        }

        System.out.printf("│[CUSTOM] Process #%d completed%n", process.getId());
    }

    private List<Processor> getTimeoutProcessors(Scheduler scheduler) {
        return scheduler.getProcessorList()
                .stream()
                .filter(this::isTimeoutProcessor)
                .sorted(this::compareProcessorTime)
                .toList();
    }

    private boolean isPowerAvailable(Scheduler s) {
        return s.getPowerConsumed() < this.initPower;
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

    private Optional<Process> getNextProcess(boolean mission) {
        var ret = mission ? this.missionQueue.pollFirst() : this.standardQueue.pollFirst();
        return Optional.ofNullable(ret);
    }

    private Optional<Process> getOppositeProcess(Process process) {
        var ret = process.isMission() ? this.standardQueue.pollFirst() : this.missionQueue.pollFirst();
        return Optional.ofNullable(ret);
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
}
