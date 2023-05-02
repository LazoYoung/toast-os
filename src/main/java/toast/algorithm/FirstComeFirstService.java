package toast.algorithm;

import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

public class FirstComeFirstService implements Algorithm {

    private final Queue<Process> readyQueue = new ArrayDeque<>();

    @Override
    public void init(Scheduler scheduler) {}

    @Override
    public void onProcessReady(Process process) {
        this.readyQueue.add(process);
        process.addCompletionListener(() -> System.out.printf("│ Process #%d completed%n", process.getId()));
    }

    @Override
    public void run(Scheduler scheduler) {
        Iterator<Processor> idleProcessors = scheduler.getIdleProcessorList().iterator();
        Iterator<Process> readyQueue = this.readyQueue.iterator();

        while (idleProcessors.hasNext() && readyQueue.hasNext()) {
            Processor processor = idleProcessors.next();
            Process process = readyQueue.next();
            processor.dispatch(process);
            readyQueue.remove();

            int pid = process.getId();
            int processorId = processor.getId();
            System.out.printf("│[FCFS] Dispatched process #%d to #%d%n", pid, processorId);
        }
    }
}
