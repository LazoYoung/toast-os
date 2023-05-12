package toast.persistence.domain;

import toast.api.Process;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FlagRecord {

    private final Map<Integer, Process> processMap = new HashMap<>();

    public Optional<Process> getFlagAtTime(int time) {
        return Optional.ofNullable(processMap.get(time));
    }

    public void setFlagAtTime(Process process, int time) {
        processMap.put(time, process);
    }

}
