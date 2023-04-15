package toast.algorithm;

import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ShortestProcessNext implements Algorithm {
    @Override
    public void run(Scheduler scheduler) {
        List<Processor> idleProcessors = scheduler.getIdleProcessorList();

        if (idleProcessors.isEmpty()) return;

        Iterator<Processor> processors = idleProcessors.iterator();
        Iterator<Process> readyQueue = scheduler.getReadyQueue().stream()
                .sorted(Comparator.comparingInt(Process::getWorkload))
                .iterator();

        while (readyQueue.hasNext() && processors.hasNext()) {
            Processor processor = processors.next();
            Process process = readyQueue.next();

            String coreName = processor.getCore().getName();
            int pid = process.getId();

            scheduler.dispatch(processor, process);
            process.addCompletionListener(() -> System.out.printf("│[SPN] Process #%d completed%n", pid));
            System.out.printf("│[SPN] Dispatched process #%d to %s%n", pid, coreName);
        }
    }
}
