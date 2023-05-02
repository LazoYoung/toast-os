package toast.algorithm;

import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.Iterator;

public class FirstComeFirstService implements Algorithm {
    @Override
    public void run(Scheduler scheduler) {
        Iterator<Processor> idleProcessors = scheduler.getIdleProcessorList().iterator();
        Iterator<Process> readyQueue = scheduler.getReadyQueue().iterator();

        while (idleProcessors.hasNext() && readyQueue.hasNext()) {
            Processor processor = idleProcessors.next();
            Process process = readyQueue.next();
            scheduler.dispatch(processor, process);
            process.addCompletionListener(() -> System.out.printf("│ Process #%d completed%n", process.getId()));
        }
    }
}
