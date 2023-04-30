package toast.algorithm;

import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class HighResponseRatioNext implements Algorithm {
    @Override
    public void run(Scheduler scheduler) {
        List<Processor> idleList = scheduler.getIdleProcessorList();

        if (idleList.isEmpty()) return;
        Iterator<Processor> processors = idleList.iterator();
        Iterator<Process> processes = scheduler.getReadyQueue().stream()
                .sorted(Comparator.comparingDouble(this::getResponseRatio).reversed())
                .iterator();

        while (processors.hasNext() && processes.hasNext()) {
            Processor processor = processors.next();
            Process process = processes.next();
            scheduler.dispatch(processor, process);
        }
    }

    private double getResponseRatio(Process process) {
        int WT = process.getWaitingTime();
        int WL = process.getWorkload();
        return (double) (WT + WL) / WL;
    }
}
