package toast.impl;

import toast.api.Algorithm;
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
    private boolean finish = false;

    public ToastTask(ToastScheduler scheduler, Algorithm algorithm) {
        this.scheduler = scheduler;
        this.algorithm = algorithm;
        newProcesses = new ArrayList<>(scheduler.getProcessList());
    }

    @Override
    public void run() {
        System.out.printf("┌ Before run: %ds\n", elapsedTime);

        enqueueProcesses();
        algorithm.run(scheduler);
        if (finish) return;

        if (newProcesses.isEmpty() && isIdle()) {
            scheduler.finish();
            System.out.print("└ End of simulation\n\n");
            printResult();
        } else {
            runProcessors();
            elapsedTime++;
            syncProcessorTime();
            System.out.printf("└ After run: %ds%n\n", elapsedTime);
        }
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
        this.cancel();
        this.finish = true;
    }

    private boolean isIdle() {
        return scheduler.getProcessorList()
                .stream()
                .allMatch(Processor::isIdle);
    }

    private void enqueueProcesses() {
        Iterator<Process> iter = newProcesses.iterator();

        while (iter.hasNext()) {
            Process process = iter.next();

            if (elapsedTime >= process.getArrivalTime()) {
                algorithm.onProcessReady(process);
                iter.remove();
            }
        }
    }

    private void runProcessors() {
        for (Processor p : scheduler.getProcessorList()) {
            ToastProcessor processor = (ToastProcessor) p;
            powerConsumed += processor.run();
        }
    }

    private void printResult() {
        System.out.println("--- Scheduling result ---");
        System.out.printf("• Elapsed time: %ds%n", scheduler.getElapsedTime());
        System.out.printf("• Power consumption: %.1fW∙s%n", scheduler.getPowerConsumed());
        //TODO RT가 아니라 TT를 구해야하지 않은지.
        System.out.printf("• Average response time: %.1fs", scheduler.getAverageResponseTime());
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
