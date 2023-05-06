package toast.persistence.domain;

import toast.api.Algorithm;
import toast.api.Core;
import toast.impl.ToastProcess;
import toast.impl.ToastProcessor;

import java.util.List;

public class SchedulerConfig {
    private Core primaryCore;
    private Algorithm algorithm;
    private List<ToastProcess> processList;
    private List<ToastProcessor> processorList;

    public SchedulerConfig(Core primaryCore, Algorithm algorithm, List<ToastProcess> processList, List<ToastProcessor> processorList) {
        this.primaryCore = primaryCore;
        this.algorithm = algorithm;
        this.processList = processList;
        this.processorList = processorList;
    }

    public Core getPrimaryCore() {
        return primaryCore;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public List<ToastProcess> getProcessList() {
        return processList;
    }

    public List<ToastProcessor> getProcessorList() {
        return processorList;
    }
}
