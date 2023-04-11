package toast.algorithm;

import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.Comparator;
import java.util.Iterator;

public class ShortestProcessNext implements Algorithm {
    @Override
    public void run(Scheduler scheduler) {
        Iterator<Processor> processors = scheduler.getIdleProcessorList().iterator();
        Iterator<Process> readyQueue = scheduler.getReadyQueue().stream()
                .sorted(Comparator.comparingInt(Process::getWorkload))
                .iterator();

        while (readyQueue.hasNext() && processors.hasNext()) {
            Processor processor = processors.next();
            Process process = readyQueue.next();
            String coreName = processor.getCore().getName();
            int pid = process.getId();

            scheduler.dispatch(processor, process);
            process.addCompletionListener(() -> System.out.printf("[SPN] Process #%d completed%n", pid));
            System.out.printf("[SPN] Dispatched process #%d to %s%n", pid, coreName);
        }

        System.out.printf("[SPN] Elapsed time: %ds%n", scheduler.getElapsedTime());
    }
}
