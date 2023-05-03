package toast.algorithm;

import toast.api.Algorithm;
import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.PriorityQueue;

public class ShortestRemainingTimeNext implements Algorithm {

    private PriorityQueue<Process> readyQueue = null;

    @Override
    public void init(Scheduler scheduler) {
        this.readyQueue = new PriorityQueue<>(Comparator.comparingInt(Process::getRemainingWorkload));
    }

    @Override
    public void onProcessReady(Process process) {
        this.readyQueue.add(process);
        process.addCompletionListener(() -> System.out.printf("│[SRTN] Process #%d completed%n", process.getId()));
    }

    @Override
    public void run(Scheduler scheduler) {
        Iterator<Processor> processors = scheduler.getProcessorList().iterator();

        if(!this.readyQueue.isEmpty()) {
            runWith(scheduler, this.readyQueue);
        }
        runWith(this.readyQueue, processors);
    }

    private static void runWith(PriorityQueue<Process> processPQ, Iterator<Processor> processors) {
        while(processors.hasNext() && !processPQ.isEmpty()) {
            Processor currentProcessor = processors.next();

            if(currentProcessor.isIdle()) {
                continue;
            }

            preempt(processPQ, currentProcessor);
        }
    }

    private static void preempt(PriorityQueue<Process> processPQ, Processor currentProcessor) {
        Optional<Process> runningProcess = currentProcessor.getRunningProcess();

        if(runningProcess.isEmpty()) {
            return;
        }

        assert processPQ.peek() != null;
        if(!hasShortTime(processPQ.peek(), runningProcess.get())) {
            return;
        }

        processPQ.add(runningProcess.get());
        currentProcessor.halt();
        dispatch(currentProcessor, processPQ.poll());
    }

    private static boolean hasShortTime(Process peekedProcess, Process runProcess) {
        return peekedProcess.getRemainingWorkload() < runProcess.getRemainingWorkload();
    }

    private static void runWith(Scheduler scheduler, PriorityQueue<Process> processorPQ) {
        Iterator<Processor> idleProcessorIterator = scheduler.getIdleProcessorList().iterator();

        while(canExecute(processorPQ, idleProcessorIterator)) {
            Processor idleProcessor = idleProcessorIterator.next();
            Process nextProcess = processorPQ.poll();

            dispatch(idleProcessor, nextProcess);
        }
    }

    private static void dispatch(Processor idleProcessor, Process nextProcess) {
        idleProcessor.dispatch(nextProcess);
        String coreName = idleProcessor.getCore().getName();
        int pid = nextProcess.getId();

        System.out.printf("│[SRTN] Dispatched process #%d to %s%n", pid, coreName);
    }

    private static boolean canExecute(PriorityQueue<Process> processorPQ, Iterator<Processor> idleProcessorIterator) {
        return !processorPQ.isEmpty() && idleProcessorIterator.hasNext();
    }
}
