package toast.impl;

import toast.algorithm.Algorithm;
import toast.api.Core;
import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.*;
import java.util.stream.Collectors;

public class ToastScheduler implements Scheduler {
    public final LinkedList<Process> readyQueue = new LinkedList<>();

    private final List<Processor> processorList;
    private final List<Process> processList;

    private final Timer timer = new Timer();

    private final Algorithm algorithm;

    private ToastTask task = null;

    private boolean started = false;

    public ToastScheduler(List<ToastProcessor> processorList, List<ToastProcess> processList, Core primaryCore,
                          Algorithm algorithm) {
        this.processorList = fillProcessors(primaryCore, processorList);
        this.processList = fillProcesses(processList);
        this.algorithm = algorithm;
    }

    private static List<Processor> fillProcessors(Core primaryCore, List<ToastProcessor> processorList) {
        return processorList.stream()
                .sorted((p1, p2) -> {
                    boolean pref1 = p1.getCore().equals(primaryCore);
                    boolean pref2 = p2.getCore().equals(primaryCore);
                    return (pref1 == pref2) ? 0 : ((pref1) ? -1 : 1);
                })
                .map(e -> (Processor) e)
                .toList();
    }

    private List<Process> fillProcesses(List<ToastProcess> processList) {
        return processList.stream()
                .map(e -> (Process) e)
                .toList();
    }

    public void start() {
        if (started) {
            throw new IllegalStateException("Scheduler already started.");
        }

        started = true;
        task = new ToastTask(this, algorithm);

        algorithm.init(this);
        timer.scheduleAtFixedRate(task, 0L, 1000L);
    }

    @Override
    public void finish() {
        if (!started) {
            throw new IllegalStateException("Scheduler not started yet.");
        }

        started = false;
        task.cancel();
        // todo collect result
    }

    @Override
    public int getElapsedTime() {
        return (task != null) ? task.getElapsedTime() : 0;
    }

    @Override
    public double getPowerConsumed() {
        return (task != null) ? task.getPowerConsumed() : 0;
    }

    @Override
    public List<Process> getReadyQueue() {
        return Collections.unmodifiableList(readyQueue);
    }

    @Override
    public List<Processor> getIdleProcessorList() {
        return getProcessorList().stream()
                .filter(Processor::isIdle)
                .collect(Collectors.toList());
    }

    @Override
    public List<Processor> getProcessorList() {
        return new ArrayList<>(processorList);
    }

    @Override
    public void dispatch(Processor processor, Process process) {
        validateProcessor(processor, false);
        validateProcess(process);

        processor.dispatch(process);
        readyQueue.remove(process);
    }

    @Override
    public void preempt(Processor processor, Process process) {
        validateProcessor(processor, true);
        validateProcess(process);

        halt(processor);
        dispatch(processor, process);
    }

    @Override
    public void halt(Processor processor) {
        Process halted = processor.halt();

        readyQueue.addLast(halted);
    }

    public List<Process> getProcessList() {
        return processList;
    }

    private void validateProcessor(Processor processor, boolean preempt) {
        if (!(processor instanceof ToastProcessor)) {
            throw new IllegalArgumentException("Failed to dispatch: incompatible processor");
        }
        if (!preempt && !processor.isIdle()) {
            throw new IllegalArgumentException("Failed to dispatch: processor already running");
        }
    }

    private void validateProcess(Process process) {
        if (!readyQueue.contains(process)) {
            throw new IllegalStateException("Failed to dispatch: process not in ready queue");
        }
    }
}
