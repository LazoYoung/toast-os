package toast.algorithm;

import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.Iterator;
import java.util.PriorityQueue;

public class HighResponseRatioNext implements Algorithm {
    private PriorityQueue<Process> readyQueue = null;

    @Override
    public void init(Scheduler scheduler) {
        this.readyQueue = new PriorityQueue<>((p1, p2) -> {
            double r1 = getResponseRatio(p1);
            double r2 = getResponseRatio(p2);
            return Double.compare(r2, r1);
        });
    }

    @Override
    public void onProcessReady(Process process) {
        this.readyQueue.add(process);
        process.addCompletionListener(() -> System.out.printf("| Process #%d completed%n", process.getId()));
    }

    @Override
    public void run(Scheduler scheduler) {
        Iterator<Processor> processorIter = scheduler.getIdleProcessorList().iterator();
        Iterator<Process> processIter = this.readyQueue.iterator();

        while (processorIter.hasNext() && processIter.hasNext()) {
            Processor processor = processorIter.next();
            Process process = processIter.next();
            processIter.remove();
            processor.dispatch(process);
            System.out.printf("| Dispatch process #%d to core #%d%n", process.getId(), processor.getId());
        }
    }

    private double getResponseRatio(Process process) {
        int WT = process.getWaitingTime();
        int BT = process.getWorkload();
        return (double) (WT + BT) / BT;
    }
}
