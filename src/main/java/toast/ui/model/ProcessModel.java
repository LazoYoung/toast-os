package toast.ui.model;

import toast.api.Process;
import toast.api.Processor;

import java.util.HashMap;
import java.util.Map;

public class ProcessModel {

    private final Process process;
    private final Map<Integer, Processor> record = new HashMap<>();

    public ProcessModel(Process process) {
        this.process = process;
    }



}
