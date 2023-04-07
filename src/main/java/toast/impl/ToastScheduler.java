package toast.impl;

import toast.algorithm.Algorithm;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.List;
import java.util.Optional;
import java.util.Timer;

public class ToastScheduler implements Scheduler {
    private final List<ToastProcessor> coreList;
    private final List<ToastProcess> processList;
    private final Timer timer = new Timer();
    private ToastTask task = null;
    private boolean started = false;

    public ToastScheduler(List<ToastProcessor> processorList, List<ToastProcess> processList) {
        this.coreList = processorList;
        this.processList = processList;
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
    public Optional<Processor> getIdleProcessor() {
        // todo method stub
        return Optional.empty();
    }

    @Override
    public List<Processor> getProcessorList() {
        // todo sort the list so that preferred cores stay up front
        return coreList.stream()
                .map(c -> (Processor) c)
                .toList();
    }
}
