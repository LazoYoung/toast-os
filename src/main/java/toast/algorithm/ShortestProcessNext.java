package toast.algorithm;

import toast.api.Process;
import toast.api.Processor;
import toast.api.Scheduler;

import java.util.Comparator;
import java.util.Optional;
import java.util.Queue;

public class ShortestProcessNext implements Algorithm {
    @Override
    public void run(Scheduler scheduler) {
        Optional<Processor> idleProcessor = scheduler.getIdleProcessor();
        Queue<Process> readyQueue = scheduler.getReadyQueue();

        if (idleProcessor.isPresent() && !readyQueue.isEmpty()) {
            Processor processor = idleProcessor.get();
            Process process = readyQueue.stream()
                    .min(Comparator.comparingInt(Process::getWorkload))
                    .orElseThrow();
            String coreName = processor.getCore().getName();
            int pid = process.getId();

            scheduler.dispatch(processor, process);
            process.addCompletionListener(() -> System.out.printf("[SPN] Process #%d completed%n", pid));
            System.out.printf("[SPN] Dispatched process #%d to %s%n", pid, coreName);
        }

        System.out.printf("[SPN] Elapsed time: %ds%n", scheduler.getElapsedTime());
    }
}
