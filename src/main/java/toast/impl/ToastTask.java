package toast.impl;

import toast.api.Algorithm;
import toast.api.Process;
import toast.api.Processor;
import toast.event.ToastEvent;
import toast.event.process.ProcessReadyEvent;
import toast.event.process.ProcessRunEvent;
import toast.event.scheduler.SchedulerFinishEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ToastTask implements Runnable {
    private final ToastScheduler scheduler;
    private final Algorithm algorithm;
    private final List<Process> newProcesses;
    private final List<Runnable> tickListeners;
    private int elapsedTime = 0;
    private double powerConsumed = 0;
    private boolean finish = false;

    public ToastTask(ToastScheduler scheduler, Algorithm algorithm) {
        this.scheduler = scheduler;
        this.algorithm = algorithm;
        this.newProcesses = new ArrayList<>(scheduler.getProcessList());
        this.tickListeners = new ArrayList<>();
    }

    @Override
    public void run() {
        System.out.printf("┌ Before run: %ds\n", elapsedTime);

        enqueueProcesses();
        algorithm.run(scheduler);
        if (finish) return;

        if (newProcesses.isEmpty() && isIdle()) {
            scheduler.finish(SchedulerFinishEvent.Cause.COMPLETE);
            System.out.print("└ End of simulation\n\n");
            printResult();
        } else {
            runProcessors();
            elapsedTime++;
            syncProcessorTime();
            System.out.printf("└ After run: %ds%n\n", elapsedTime);
        }

        tickListeners.forEach(Runnable::run);
    }

    private void syncProcessorTime() {
        for (Processor p : scheduler.getProcessorList()) {
            ToastProcessor processor = (ToastProcessor) p;
            processor.updateTime(this.elapsedTime);
        }
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public double getPowerConsumed() {
        return powerConsumed;
    }

    public void finish() {
        this.finish = true;
    }

    public void addTickListener(Runnable runnable) {
        this.tickListeners.add(runnable);
    }

    private boolean isIdle() {
        return scheduler.getActiveProcessorList()
                .stream()
                .allMatch(Processor::isIdle);
    }

    private void enqueueProcesses() {
        Iterator<Process> iter = newProcesses.iterator();

        while (iter.hasNext()) {
            Process process = iter.next();

            if (elapsedTime >= process.getArrivalTime()) {
                var event = new ProcessReadyEvent(process, elapsedTime);
                ToastEvent.dispatch(event.getClass(), event);
                algorithm.onProcessReady(process);
                iter.remove();
            }
        }
    }

    private void runProcessors() {
        for (Processor p : scheduler.getActiveProcessorList()) {
            ToastProcessor processor = (ToastProcessor) p;
            powerConsumed += processor.run();

            processor.getRunningProcess().ifPresent(process -> {
                var event = new ProcessRunEvent(process, elapsedTime, processor);
                ToastEvent.dispatch(event.getClass(), event);
            });
        }
    }

    private void printResult() {
        System.out.println("--- Scheduling result ---");
        System.out.printf("• Elapsed time: %ds%n", scheduler.getElapsedTime());
        System.out.printf("• Power consumption: %.1fW∙s%n", scheduler.getPowerConsumed());
        System.out.printf("• Average turnaround time: %.1fs", scheduler.getAverageTT());
        System.out.println();
        System.out.println("--- Process result ---");

        for (Process process : scheduler.getProcessList()) {
            String type = process.isMission() ? "Mission" : "Standard";
            System.out.printf("%s Process #%s%n", type, process.getId());
            System.out.printf("• Waiting time: %ds%n", process.getWaitingTime());
            System.out.printf("• Turnaround time: %ds%n", process.getTurnaroundTime());
            System.out.printf("• Normalized TT: %.2f%n", process.getNormalizedTurnaroundTime());
        }
    }
}
