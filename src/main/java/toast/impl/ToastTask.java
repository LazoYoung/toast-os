package toast.impl;

import toast.algorithm.Algorithm;
import toast.api.Process;
import toast.api.Processor;

import java.util.*;

public class ToastTask extends TimerTask {
    private final ToastScheduler scheduler;
    private final Algorithm algorithm;
    private final List<Process> newProcesses;
    private int elapsedTime = 0;

    public ToastTask(ToastScheduler scheduler, Algorithm algorithm) {
        this.scheduler = scheduler;
        this.algorithm = algorithm;
        newProcesses = new ArrayList<>(scheduler.getProcessList());
    }

    @Override
    public void run() {
        Queue<Process> readyQueue = scheduler.getReadyQueue();

        enqueueProcesses(readyQueue);
        algorithm.run(scheduler);
        int activeProcessors = runProcessors();
        updateWaitingProcesses();

        if (activeProcessors == 0 && newProcesses.isEmpty() && readyQueue.isEmpty()) {
            scheduler.finish();
        } else {
            elapsedTime++;
        }
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    private void enqueueProcesses(Queue<Process> readyQueue) {
        Iterator<Process> iter = newProcesses.iterator();

        while (iter.hasNext()) {
            Process process = iter.next();

            if (process.getArrivalTime() <= elapsedTime) {
                readyQueue.add(process);
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
            processor.run();

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
}
