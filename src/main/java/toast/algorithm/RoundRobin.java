package toast.algorithm;

import java.util.Iterator;
import java.util.List;
import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

public class RoundRobin implements Algorithm {
    private final Integer timeQuantum;

    public RoundRobin(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    @Override
    public void run(Scheduler scheduler) {
        Iterator<Process> readyQueueIterator = getReadyQueueIterator(scheduler);
        //동시 접근 방지 위해 상단에 배치
        boolean hasWait = readyQueueIterator.hasNext();

        List<Processor> timeOverProcessors = getTimeOverProcessors(scheduler.getProcessorList());

        runWith(scheduler, readyQueueIterator, timeOverProcessors);

        if(hasWait) {
            runWith(scheduler, readyQueueIterator, scheduler.getIdleProcessorList().iterator());
        }

        System.out.printf("│[SPN] Elapsed time: %ds%n", scheduler.getElapsedTime());
    }

    private static void runWith(Scheduler scheduler, Iterator<Process> readyQueueIterator,
                                  Iterator<Processor> idleProcessorIterator) {
        while(canExecute(readyQueueIterator, idleProcessorIterator)) {
            Processor idleProcessor = idleProcessorIterator.next();
            Process nextProcess = readyQueueIterator.next();

            dispatch(scheduler, idleProcessor, nextProcess);
        }
    }

    private static void runWith(Scheduler scheduler, Iterator<Process> readyQueueIterator,
                                  List<Processor> timeOverProcessors) {
        for (Processor timeOverProcessor : timeOverProcessors) {
            scheduler.halt(timeOverProcessor);

            assert(readyQueueIterator.hasNext());
            Process nextProcess = readyQueueIterator.next();

            dispatch(scheduler, timeOverProcessor, nextProcess);
        }
    }

    private List<Processor> getTimeOverProcessors(List<Processor> processors) {
        return processors.stream()
                .filter(RoundRobin::hasRunningProcess)
                .filter(this::isOverContinuousBurstTime)
                .toList();
    }

    private static Iterator<Process> getReadyQueueIterator(Scheduler scheduler) {
        return scheduler.getReadyQueue().stream().iterator();
    }

    private static void dispatch(Scheduler scheduler, Processor idleProcessor, Process nextProcess) {
        scheduler.dispatch(idleProcessor, nextProcess);
        String coreName = idleProcessor.getCore().getName();
        int pid = nextProcess.getId();


        if(isFirstRun(nextProcess)) {
            nextProcess.addCompletionListener(() -> System.out.printf("│[SPN] Process #%d completed%n", pid));
        }

        System.out.printf("│[SPN] Dispatched process #%d to %s%n", pid, coreName);
    }

    private static boolean isFirstRun(Process nextProcess) {
        return nextProcess.getWorkload() == nextProcess.getRemainingWorkload();
    }

    private static boolean canExecute(Iterator<Process> readyQueueIterator, Iterator<Processor> idleProcessorIterator) {
        return idleProcessorIterator.hasNext() && readyQueueIterator.hasNext();
    }

    private boolean isOverContinuousBurstTime(Processor processor) {
        assert(processor.getRunningProcess().isPresent());

        return processor.getRunningProcess().get().getContinuousBurstTime() >= timeQuantum;
    }

    private static boolean hasRunningProcess(Processor processor) {
        return processor.getRunningProcess().isPresent();
    }
}
