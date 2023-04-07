package toast.impl;

import toast.algorithm.Algorithm;
import toast.api.Core;
import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.*;

public class ToastScheduler implements Scheduler {
    private final List<Processor> processorList;
    private final List<Process> processList;
    private final Queue<Process> readyQueue = new LinkedList<>();
    private final Timer timer = new Timer();
    private ToastTask task = null;
    private boolean started = false;

    public ToastScheduler(Core primaryCore, List<ToastProcessor> processorList, List<ToastProcess> processList) {
        this.processorList = processorList.stream()
                .sorted((p1, p2) -> {
                    boolean pref1 = p1.getCore().equals(primaryCore);
                    boolean pref2 = p2.getCore().equals(primaryCore);
                    return (pref1 == pref2) ? 0 : ((pref1) ? -1 : 1);
                })
                .map(e -> (Processor) e)
                .toList();
        this.processList = processList.stream()
                .map(e -> (Process) e)
                .toList();
    }

    public void start(Algorithm algorithm) {
        if (started) {
            throw new IllegalStateException("Scheduler already started.");
        }

        started = true;
        task = new ToastTask(this, algorithm);
        timer.scheduleAtFixedRate(task, 0L, 1000L);
    }

    public void stop() {
        if (!started) {
            throw new IllegalStateException("Scheduler not started yet.");
        }

        started = false;
        task.cancel();
    }

    @Override
    public int getElapsedTime() {
        return (task != null) ? task.getElapsedTime() : 0;
    }

    @Override
    public Queue<Process> getReadyQueue() {
        return readyQueue;
    }

    @Override
    public Optional<Processor> getIdleProcessor() {
        return processorList.stream()
                .filter(Processor::isIdle)
                .findFirst();
    }

    @Override
    public List<Processor> getProcessorList() {
        return processorList;
    }

    public List<Process> getProcessList() {
        return processList;
    }
}
