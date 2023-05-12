package toast.persistence.domain;

import toast.algorithm.*;
import toast.api.Algorithm;
import toast.api.Core;
import toast.enums.AlgorithmName;
import toast.impl.ToastProcess;
import toast.impl.ToastProcessor;

import java.util.List;
import java.util.stream.Collectors;

public class SchedulerConfig {
    private final Integer timeQuantum;
    private final Double initPower;
    private final Double powerThreshold;
    private final Core primaryCore;
    private Algorithm algorithm;
    private List<ToastProcess> processList;
    private List<ToastProcessor> processorList;

    public SchedulerConfig(Core primaryCore, Integer timeQuantum, Double initPower, Double powerThreshold) {
        this.primaryCore = primaryCore;
        this.timeQuantum = timeQuantum;
        this.initPower = initPower;
        this.powerThreshold = powerThreshold;
    }

    public Core getPrimaryCore() {
        return primaryCore;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public List<ToastProcess> getProcessList() {
        return processList.stream()
                .map(ToastProcess::clone)
                .collect(Collectors.toList());
    }

    public List<ToastProcessor> getProcessorList() {
        return processorList.stream()
                .map(ToastProcessor::clone)
                .collect(Collectors.toList());
    }

    public Integer getTimeQuantum() {
        return timeQuantum;
    }

    public Double getInitPower() {
        return initPower;
    }

    public Double getPowerThreshold() {
        return powerThreshold;
    }

    public void setAlgorithm(AlgorithmName algorithmName) {
        this.algorithm = switch (algorithmName) {
            case FCFS -> new FirstComeFirstService();
            case RR -> new RoundRobin(timeQuantum);
            case HRRN -> new HighResponseRatioNext();
            case SPN -> new ShortestProcessNext();
            case SRTN -> new ShortestRemainingTimeNext();
            case CUSTOM -> new CustomSatellite(timeQuantum, initPower, powerThreshold);
        };
    }

    public void setProcessList(List<ToastProcess> processList) {
        if (processList.stream().anyMatch(ToastProcess::isRunning)) {
            throw new IllegalArgumentException("Running process is not accepted!");
        }

        this.processList = processList;
    }

    public void setProcessorList(List<ToastProcessor> processorList) {
        if (processorList.stream().anyMatch(ToastProcessor::isRunning)) {
            throw new IllegalArgumentException("Running processor is not accepted!");
        }

        this.processorList = processorList;
    }
}
