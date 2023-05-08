package toast.persistence.domain;

import toast.api.Process;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProcessorRecord {
    private final Map<Integer, Process> processMap = new HashMap<>();

    public Optional<Process> getProcessAtTime(int time) {
        return Optional.ofNullable(processMap.get(time));
    }

    public void setProcessAtTime(Process process, int time) {
        processMap.put(time, process);
    }
}
