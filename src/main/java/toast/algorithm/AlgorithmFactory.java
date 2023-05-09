package toast.algorithm;

import toast.api.Algorithm;
import toast.enums.AlgorithmName;

public class AlgorithmFactory {
    private final AlgorithmName name;

    public AlgorithmFactory(AlgorithmName name) {
        this.name = name;
    }

    public Algorithm create(Integer timeQuantum, Double initPower, Double powerThreshold) {
        return switch (this.name) {
            case FCFS -> new FirstComeFirstService();
            case RR -> new RoundRobin(timeQuantum);
            case HRRN -> new HighResponseRatioNext();
            case SPN -> new ShortestProcessNext();
            case SRTN -> new ShortestRemainingTimeNext();
            case CUSTOM -> new CustomSatellite(timeQuantum, initPower, powerThreshold);
        };
    }
}
