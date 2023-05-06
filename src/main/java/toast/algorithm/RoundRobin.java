package toast.algorithm;

import toast.api.Algorithm;
import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;
import toast.event.ToastEvent;
import toast.event.process.ProcessPreemptEvent;

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
        List<Processor> timeOverProcessors = getTimeOverProcessors(scheduler.getActiveProcessorList());

        runWith(timeOverProcessors, scheduler);

        if (!this.readyQueue.isEmpty()) {
            runWith(scheduler.getIdleProcessorList().iterator());
        }
    }

    private void runWith(Iterator<Processor> idleProcessorIterator) {
        while (canExecute(idleProcessorIterator)) {
            Processor idleProcessor = idleProcessorIterator.next();
            Process nextProcess = this.readyQueue.poll();

            dispatch(idleProcessor, nextProcess);
        }
    }

    private void runWith(List<Processor> timeOverProcessors, Scheduler scheduler) {
        for (Processor timeOverProcessor : timeOverProcessors) {

            Process halted = timeOverProcessor.halt();
            this.readyQueue.add(halted);
            Process nextProcess = this.readyQueue.poll();
            dispatch(timeOverProcessor, nextProcess);

            // dispatch event
            int time = scheduler.getElapsedTime();
            var event = new ProcessPreemptEvent(halted, time, nextProcess);
            ToastEvent.dispatch(event.getClass(), event);
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
