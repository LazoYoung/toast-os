package toast.impl;

import toast.api.Algorithm;
import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;
import toast.event.ToastEvent;
import toast.event.scheduler.SchedulerFinishEvent;
import toast.event.scheduler.SchedulerStartEvent;
import toast.persistence.domain.SchedulerConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("removal")
public class ToastScheduler implements Scheduler {
    private static final ToastScheduler instance = new ToastScheduler();
    // todo deprecated element
    public final LinkedList<Process> readyQueue = new LinkedList<>();
    private final ToastRecorder recorder = new ToastRecorder(this);
    private final List<Processor> processorList = new ArrayList<>();
    private final List<Process> processList = new ArrayList<>();
    private Algorithm algorithm = null;
    private ToastTask task = null;
    private ScheduledFuture<?> taskFuture = null;
    private boolean started = false;

    public static ToastScheduler getInstance() {
        return instance;
    }

    private void populateProcessor(SchedulerConfig config) {
        var primaryCore = config.getPrimaryCore();
        var list = config.getProcessorList()
                .stream()
                .sorted((p1, p2) -> {
                    boolean pref1 = p1.getCore().equals(primaryCore);
                    boolean pref2 = p2.getCore().equals(primaryCore);
                    return (pref1 == pref2) ? 0 : ((pref1) ? -1 : 1);
                })
                .map(e -> (Processor) e)
                .toList();
        this.processorList.clear();
        this.processorList.addAll(list);
    }

    private void populateProcess(SchedulerConfig config) {
        var list = config.getProcessList()
                .stream()
                .map(e -> (Process) e)
                .toList();
        this.processList.clear();
        this.processList.addAll(list);
    }

    public void start(SchedulerConfig config) {
        if (this.started) {
            throw new IllegalStateException("Scheduler already started.");
        }

        populateProcessor(config);
        populateProcess(config);
        this.algorithm = config.getAlgorithm();
        this.started = true;
        this.task = new ToastTask(this);
        this.taskFuture = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this.task, 0L, 1, TimeUnit.SECONDS);
        recorder.startLoggingEvents();
        recorder.startRecording();

        // dispatch event
        var event = new SchedulerStartEvent();
        ToastEvent.dispatch(event.getClass(), event);
    }

    @Override
    public void finish(SchedulerFinishEvent.Cause cause) {
        if (!started) {
            throw new IllegalStateException("Scheduler not started yet.");
        }

        started = false;
        task.finish();
        taskFuture.cancel(false);
        recorder.stopLoggingEvents();
        recorder.eraseRecords();

        // dispatch event
        var event = new SchedulerFinishEvent(cause);
        ToastEvent.dispatch(event.getClass(), event);
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
    public double getAverageTT() {
        int sum = processList.stream()
                .mapToInt(Process::getTurnaroundTime)
                .sum();
        return (double) sum / processList.size();
    }

    @Override
    public List<Process> getReadyQueue() {
        return Collections.unmodifiableList(readyQueue);
    }

    @Override
    public List<Processor> getProcessorList() {
        return new ArrayList<>(processorList);
    }

    @Override
    public List<Processor> getIdleProcessorList() {
        return getActiveProcessorList()
                .stream()
                .filter(Processor::isIdle)
                .collect(Collectors.toList());
    }

    @Override
    public List<Processor> getActiveProcessorList() {
        return processorList.stream()
                .filter(Processor::isActive)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Process> getProcessList() {
        return processList;
    }

    @Override
    public void dispatch(Processor processor, Process process) {
        validateProcessor(processor, false);
        validateProcess(process);

        processor.dispatch(process);
        readyQueue.remove(process);

        int pid = process.getId();
        String coreName = processor.getCore().getName();
        System.out.printf("│ Dispatched process #%d to %s%n", pid, coreName);
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

    public void addTickListener(Runnable runnable) {
        task.addTickListener(runnable);
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

    public Algorithm getAlgorithm() {
        return algorithm;
    }
}
