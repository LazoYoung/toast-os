package toast.impl;

import toast.api.Algorithm;
import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;
import toast.event.ToastEvent;
import toast.event.processor.ProcessorDeactivateEvent;
import toast.event.scheduler.SchedulerFinishEvent;
import toast.event.scheduler.SchedulerStartEvent;
import toast.persistence.domain.SchedulerConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("removal")
public class ToastScheduler implements Scheduler {
    private static final ToastScheduler instance = new ToastScheduler();
    private final ToastRecorder recorder = new ToastRecorder(this);
    private final List<Processor> processorList = new ArrayList<>();
    private final List<Process> processList = new ArrayList<>();
    private SchedulerConfig config = null;
    private Algorithm algorithm = null;
    private ToastTask task = null;
    private ScheduledFuture<?> taskFuture = null;
    private boolean running = false;

    public static ToastScheduler getInstance() {
        return instance;
    }

    public ToastScheduler setup(SchedulerConfig config) {
        this.config = config;
        return this;
    }

    public void start() {
        if (this.running) {
            throw new RuntimeException("Scheduler already started.");
        }
        if (this.config == null) {
            throw new IllegalStateException("Scheduler is not configured.");
        }

        populateProcessor(this.config);
        populateProcess(this.config);
        this.algorithm = this.config.getAlgorithm();
        this.running = true;
        this.recorder.eraseRecords();
        this.recorder.startRecording();
        this.recorder.startLoggingEvents();

        this.task = new ToastTask(this);
        this.taskFuture = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this.task, 0L, 1, TimeUnit.SECONDS);

        // dispatch event
        var event = new SchedulerStartEvent(this);
        ToastEvent.dispatch(event.getClass(), event);
    }

    @Override
    public void finish(SchedulerFinishEvent.Cause cause) {
        if (!running) {
            throw new IllegalStateException("Scheduler not started yet.");
        }

        deactivateProcessors();
        running = false;
        task.finish();
        taskFuture.cancel(false);
        recorder.stopLoggingEvents();

        // dispatch event
        var event = new SchedulerFinishEvent(this, cause);
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
        if (processList.size() == 0) return 0;

        int sum = processList.stream()
                .filter(Process::isComplete)
                .mapToInt(Process::getTurnaroundTime)
                .sum();
        return (double) sum / processList.size();
    }

    @Override
    public List<Process> getReadyQueue() {
        return Collections.emptyList();
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

        processor.dispatch(process);

        int pid = process.getId();
        String coreName = processor.getCore().getName();
        System.out.printf("│ Dispatched process #%d to %s%n", pid, coreName);
    }

    @Override
    public void preempt(Processor processor, Process process) {
        validateProcessor(processor, true);

        halt(processor);
        dispatch(processor, process);
    }

    @Override
    public void halt(Processor processor) {
        processor.halt();
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public SchedulerConfig getConfig() {
        return config;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isConfigured() {
        return config != null;
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

    private void validateProcessor(Processor processor, boolean preempt) {
        if (!(processor instanceof ToastProcessor)) {
            throw new IllegalArgumentException("Failed to dispatch: incompatible processor");
        }
        if (!preempt && !processor.isIdle()) {
            throw new IllegalArgumentException("Failed to dispatch: processor already running");
        }
    }

    private void deactivateProcessors() {
        getActiveProcessorList().forEach(p -> p.deactivate(ProcessorDeactivateEvent.Cause.FINISH));
    }
}
