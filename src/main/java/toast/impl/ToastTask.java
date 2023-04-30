package toast.impl;

import toast.algorithm.Algorithm;
import toast.api.Process;
import toast.api.Processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

public class ToastTask extends TimerTask {
    private final ToastScheduler scheduler;
    private final Algorithm algorithm;
    private final List<Process> newProcesses;
    private int elapsedTime = 0;
    private double powerConsumed = 0;

    public ToastTask(ToastScheduler scheduler, Algorithm algorithm) {
        this.scheduler = scheduler;
        this.algorithm = algorithm;
        newProcesses = new ArrayList<>(scheduler.getProcessList());
    }

    @Override
    public void run() {
        enqueueProcesses();
        System.out.printf("┌ Before run: %ds\n", elapsedTime);
        algorithm.run(scheduler);
        int activeProcessors = runProcessors();
        System.out.printf("└ After run: %ds%n\n", ++elapsedTime);
        updateWaitingProcesses();

        if (activeProcessors == 0 && newProcesses.isEmpty() && scheduler.readyQueue.isEmpty()) {
            scheduler.finish();
            printResult();
        }
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public double getPowerConsumed() {
        return powerConsumed;
    }

    private void enqueueProcesses() {
        Iterator<Process> iter = newProcesses.iterator();

        while (iter.hasNext()) {
            Process process = iter.next();

            if (elapsedTime >= process.getArrivalTime()) {
                scheduler.readyQueue.add(process);
                algorithm.onProcessReady(process);
                iter.remove();
            }
        }
    }

    /**
     * @return number of active processors
     */
    private int runProcessors() {
        int active = 0;

        for (Processor p : scheduler.getProcessorList()) {
            ToastProcessor processor = (ToastProcessor) p;
            powerConsumed += processor.run();

            if (!p.isIdle()) {
                active++;
            }
        }

        return active;
    }

    private void updateWaitingProcesses() {
        for (Process p : scheduler.getReadyQueue()) {
            ToastProcess process = (ToastProcess) p;
            process.standby();
        }
    }

    private void printResult() {
        System.out.println("--- Scheduling result ---");
        System.out.printf("• Elapsed time: %ds%n", scheduler.getElapsedTime());
        System.out.printf("• Power consumed: %.1fW%n", scheduler.getPowerConsumed());
        System.out.println();
        System.out.println("--- Process result ---");

        for (Process process : scheduler.getProcessList()) {
            String type = process.isMission() ? "Mission" : "Standard";
            System.out.printf("%s Process #%s%n", type, process.getId());
            System.out.printf("• Arrival time: %ds%n", process.getArrivalTime());
            System.out.printf("• Waiting time: %ds%n", process.getWaitingTime());
            System.out.printf("• Turnaround time: %ds%n", process.getTurnaroundTime());
            System.out.printf("• Normalized TT: %.2f%n", process.getNormalizedTurnaroundTime());
        }
    }
}
