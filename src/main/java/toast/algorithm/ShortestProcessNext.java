package toast.algorithm;

import toast.api.Algorithm;
import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class ShortestProcessNext implements Algorithm {

    private PriorityQueue<Process> readyQueue = null;

    @Override
    public void init(Scheduler scheduler) {
        this.readyQueue = new PriorityQueue<>(Comparator.comparingInt(Process::getWorkload));
    }

    @Override
    public void onProcessReady(Process process) {
        this.readyQueue.add(process);
    }

    @Override
    public void run(Scheduler scheduler) {
        List<Processor> idleProcessors = scheduler.getIdleProcessorList();

        if (idleProcessors.isEmpty()) return;

        Iterator<Processor> processors = idleProcessors.iterator();
        Iterator<Process> readyQueue = this.readyQueue.iterator();

        while (readyQueue.hasNext() && processors.hasNext()) {
            Processor processor = processors.next();
            Process process = readyQueue.next();
            processor.dispatch(process);
            readyQueue.remove();
        }
    }

    @Override
    public Iterator<Process> getStandardQueue() {
        return readyQueue.iterator();
    }
}
