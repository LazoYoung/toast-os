package toast.impl;

import toast.api.Process;
import toast.api.*;
import toast.event.ToastEvent;
import toast.event.process.ProcessCompleteEvent;
import toast.event.process.ProcessDispatchEvent;
import toast.event.process.ProcessPreemptEvent;
import toast.event.process.ProcessReadyEvent;
import toast.event.processor.ProcessorDeactivateEvent;
import toast.event.processor.ProcessorRebootEvent;
import toast.event.scheduler.SchedulerFinishEvent;
import toast.event.scheduler.SchedulerStartEvent;

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
    // todo deprecated element
    public final LinkedList<Process> readyQueue = new LinkedList<>();
    private final List<Integer> listenerList = new ArrayList<>();
    private final List<Processor> processorList;
    private final List<Process> processList;
    private final Algorithm algorithm;
    private ToastTask task = null;
    private ScheduledFuture<?> taskFuture = null;

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
        taskFuture = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(task, 0L, 1, TimeUnit.SECONDS);
        algorithm.init(this);

        // dispatch event
        var event = new SchedulerStartEvent();
        ToastEvent.dispatch(event.getClass(), event);

        // start logging
        startLoggingEvents();
    }

    @Override
    public void finish(SchedulerFinishEvent.Cause cause) {
        if (!started) {
            throw new IllegalStateException("Scheduler not started yet.");
        }

        started = false;
        task.finish();
        taskFuture.cancel(false);

        // dispatch event
        var event = new SchedulerFinishEvent(cause);
        ToastEvent.dispatch(event.getClass(), event);

        // stop logging
        stopLoggingEvents();
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

    private void startLoggingEvents() {
        int l1 = ToastEvent.registerListener(ProcessDispatchEvent.class, (ProcessDispatchEvent event) -> {
            Processor processor = event.getProcessor();
            String coreName = processor.getCore().getName();
            int coreId = processor.getId();
            int pid = event.getProcess().getId();
            System.out.printf("| Dispatch process #%d to %s #%d%n", pid, coreName, coreId);
        });

        int l2 = ToastEvent.registerListener(ProcessCompleteEvent.class, (ProcessCompleteEvent event) -> {
            System.out.printf("| Process #%d complete%n", event.getProcess().getId());
        });

        int l3 = ToastEvent.registerListener(ProcessPreemptEvent.class, (ProcessPreemptEvent event) -> {
            int from = event.getProcess().getId();
            int to = event.getNextProcess().getId();
            System.out.printf("| Preempt process #%d → #%d%n", from, to);
        });

        int l4 = ToastEvent.registerListener(ProcessReadyEvent.class, (ProcessReadyEvent event) -> {
            System.out.printf("| Process #%d is ready%n", event.getProcess().getId());
        });

        int l5 =  ToastEvent.registerListener(ProcessorDeactivateEvent.class, (ProcessorDeactivateEvent event) -> {
            Processor processor = event.getProcessor();
            String coreName = processor.getCore().getName();
            int id = processor.getId();
            String cause = switch (event.getCause()) {
                case POWER_LOSS -> "power loss";
                case POWER_SAVING -> "power saving";
            };
            System.out.printf("| %s #%d deactivated due to %s%n", coreName, id, cause);
        });

        int l6 = ToastEvent.registerListener(ProcessorRebootEvent.class, (ProcessorRebootEvent event) -> {
            Processor processor = event.getProcessor();
            String coreName = processor.getCore().getName();
            int id = processor.getId();
            System.out.printf("| Reboot %s #%d%n", coreName, id);
        });

        Collections.addAll(this.listenerList, l1, l2, l3, l4, l5, l6);
    }

    private void stopLoggingEvents() {
        this.listenerList.forEach(ToastEvent::unregisterListener);
        this.listenerList.clear();
    }
}
