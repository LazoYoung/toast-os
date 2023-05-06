package toast.ui.model;

import toast.api.Process;
import toast.api.Processor;

import java.util.HashMap;
import java.util.Map;

public class ProcessRecord {

    private final Process process;
    private final Map<Integer, Processor> record = new HashMap<>();

    public ProcessRecord(Process process) {
        this.process = process;
    }

}
