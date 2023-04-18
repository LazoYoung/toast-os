package toast.algorithm;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.PriorityQueue;
import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

public class ShortestRemainingTimeNext implements Algorithm {
    @Override
    public void run(Scheduler scheduler) {
        PriorityQueue<Process> processPQ = new PriorityQueue<>(new ProcessComparator());
        processPQ.addAll(scheduler.getReadyQueue());
        Iterator<Processor> processors = scheduler.getProcessorList().iterator();

        if(!processPQ.isEmpty()) {
            runWith(scheduler, processPQ);
        }
        runWith(scheduler, processPQ, processors);
    }

    private static void runWith(Scheduler scheduler, PriorityQueue<Process> processPQ,
                                  Iterator<Processor> processors) {
        while(processors.hasNext() && !processPQ.isEmpty()) {
            Processor currentProcessor = processors.next();

            if(currentProcessor.isIdle()) {
                continue;
            }

            preempt(scheduler, processPQ, currentProcessor);
        }
    }

    private static void preempt(Scheduler scheduler, PriorityQueue<Process> processPQ,
                                Processor currentProcessor) {
        Optional<Process> runningProcess = currentProcessor.getRunningProcess();

        if(runningProcess.isEmpty()) {
            return;
        }

        assert processPQ.peek() != null;
        if(!hasShortTime(processPQ.peek(), runningProcess.get())) {
            return;
        }

        processPQ.add(currentProcessor.getRunningProcess().get());
        scheduler.halt(currentProcessor);
        dispatch(scheduler, currentProcessor, processPQ.poll());
    }

    private static boolean hasShortTime(Process peekedProcess, Process runProcess) {
        return peekedProcess.getRemainingWorkload() < runProcess.getRemainingWorkload();
    }

    private static void runWith(Scheduler scheduler, PriorityQueue<Process> processorPQ) {
        Iterator<Processor> idleProcessorIterator = scheduler.getIdleProcessorList().iterator();

        while(canExecute(processorPQ, idleProcessorIterator)) {
            Processor idleProcessor = idleProcessorIterator.next();
            Process nextProcess = processorPQ.poll();

            dispatch(scheduler, idleProcessor, nextProcess);
        }
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

    private static boolean canExecute(PriorityQueue<Process> processorPQ, Iterator<Processor> idleProcessorIterator) {
        return !processorPQ.isEmpty() && idleProcessorIterator.hasNext();
    }

    class ProcessComparator implements Comparator<Process> {

        @Override
        public int compare(Process e1, Process e2) {
            return Integer.compare(e1.getRemainingWorkload(), e2.getRemainingWorkload());
        }
    }
}
