package toast.algorithm;

import toast.api.Algorithm;
import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class RoundRobin implements Algorithm {
    private final Integer timeQuantum;
    private Queue<Process> readyQueue = null;

    public RoundRobin(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    @Override
    public void init(Scheduler scheduler) {
        this.readyQueue = new ArrayDeque<>();
    }

    @Override
    public void onProcessReady(Process process) {
        this.readyQueue.add(process);
    }

    @Override
    public void run(Scheduler scheduler) {
        List<Processor> timeOverProcessors = getTimeOverProcessors(scheduler.getProcessorList());

        runWith(timeOverProcessors);

        if (!this.readyQueue.isEmpty()) {
            runWith(scheduler.getIdleProcessorList().iterator());
        }

        System.out.printf("│[SPN] Elapsed time: %ds%n", scheduler.getElapsedTime());
    }

    private void runWith(Iterator<Processor> idleProcessorIterator) {
        while (canExecute(idleProcessorIterator)) {
            Processor idleProcessor = idleProcessorIterator.next();
            Process nextProcess = this.readyQueue.poll();

            dispatch(idleProcessor, nextProcess);
        }
    }

    private void runWith(List<Processor> timeOverProcessors) {
        for (Processor timeOverProcessor : timeOverProcessors) {

            this.readyQueue.add(timeOverProcessor.halt());
            Process nextProcess = this.readyQueue.poll();
            dispatch(timeOverProcessor, nextProcess);
        }
    }

    private List<Processor> getTimeOverProcessors(List<Processor> processors) {
        return processors.stream()
                .filter(RoundRobin::hasRunningProcess)
                .filter(this::isOverContinuousBurstTime)
                .toList();
    }

    private static void dispatch(Processor idleProcessor, Process nextProcess) {
        idleProcessor.dispatch(nextProcess);
        String coreName = idleProcessor.getCore().getName();
        int pid = nextProcess.getId();

        if (isFirstRun(nextProcess)) {
            nextProcess.addCompletionListener(() -> System.out.printf("│[SPN] Process #%d completed%n", pid));
        }

        System.out.printf("│[SPN] Dispatched process #%d to %s%n", pid, coreName);
    }

    private static boolean isFirstRun(Process nextProcess) {
        return nextProcess.getWorkload() == nextProcess.getRemainingWorkload();
    }

    private boolean canExecute(Iterator<Processor> idleProcessorIterator) {
        return idleProcessorIterator.hasNext() && !this.readyQueue.isEmpty();
    }

    private boolean isOverContinuousBurstTime(Processor processor) {
        return processor.getRunningProcess().get().getContinuousBurstTime() >= timeQuantum;
    }

    private static boolean hasRunningProcess(Processor processor) {
        return processor.getRunningProcess().isPresent();
    }
}
