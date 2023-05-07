package toast.persistence.domain;

import toast.api.Processor;

import java.util.HashMap;
import java.util.Map;

public class SchedulerRecord {

    private static final SchedulerRecord instance = new SchedulerRecord();
    private final Map<Integer, ProcessorRecord> processorData = new HashMap<>();

    public static SchedulerRecord getInstance() {
        return instance;
    }

    public ProcessorRecord getProcessorRecord(Processor processor) {
        return processorData.get(processor.getId());
    }

    public void addProcessorRecord(Processor processor) {
        int id = processor.getId();

        if (this.processorData.containsKey(id)) {
            throw new IllegalArgumentException("Process is already added.");
        }

        processorData.put(id, new ProcessorRecord());
    }

    public void clear() {
        processorData.clear();
    }

}
