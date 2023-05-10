package toast.algorithm;

import toast.api.Algorithm;
import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;
import toast.event.ToastEvent;
import toast.event.process.ProcessPreemptEvent;

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
    }

    @Override
    public void run(Scheduler scheduler) {
        Iterator<Processor> processors = scheduler.getActiveProcessorList().iterator();

        if(!this.readyQueue.isEmpty()) {
            runWith(scheduler, this.readyQueue);
        }
        runWith(this.readyQueue, processors, scheduler);
    }

    @Override
    public Iterator<Process> getStandardQueue() {
        return readyQueue.iterator();
    }

    private static void runWith(PriorityQueue<Process> processPQ, Iterator<Processor> processors, Scheduler scheduler) {
        while(processors.hasNext() && !processPQ.isEmpty()) {
            Processor currentProcessor = processors.next();

            if(currentProcessor.isIdle()) {
                continue;
            }

            preempt(processPQ, currentProcessor, scheduler);
        }
    }

    private static void preempt(PriorityQueue<Process> processPQ, Processor currentProcessor, Scheduler scheduler) {
        Optional<Process> runningProcess = currentProcessor.getRunningProcess();

        if(runningProcess.isEmpty()) {
            return;
        }

        assert processPQ.peek() != null;
        if(!hasShortTime(processPQ.peek(), runningProcess.get())) {
            return;
        }

        Process halted = currentProcessor.halt();
        processPQ.add(halted);
        Process nextProcess = processPQ.poll();
        dispatch(currentProcessor, nextProcess);

        // dispatch event
        int time = scheduler.getElapsedTime();
        var event = new ProcessPreemptEvent(halted, time, nextProcess);
        ToastEvent.dispatch(event.getClass(), event);
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
    }

    private static boolean canExecute(PriorityQueue<Process> processorPQ, Iterator<Processor> idleProcessorIterator) {
        return !processorPQ.isEmpty() && idleProcessorIterator.hasNext();
    }
}
