package toast.persistence.domain;

import toast.api.Processor;

import java.util.HashMap;
import java.util.Map;

public class Record {

    private static final Record instance = new Record();
    private final Map<Integer, ProcessorRecord> processorData = new HashMap<>();

    public static Record getInstance() {
        return instance;
    }

    public ProcessorRecord getProcessorRecord(Processor processor) {
        return getProcessorRecord(processor.getId());
    }

    public ProcessorRecord getProcessorRecord(int id) {
        return processorData.get(id);
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
