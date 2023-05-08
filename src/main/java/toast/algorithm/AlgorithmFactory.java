package toast.algorithm;

import toast.api.Algorithm;
import toast.enums.AlgorithmName;

public class AlgorithmFactory {
    private final AlgorithmName name;

    public AlgorithmFactory(AlgorithmName name) {
        this.name = name;
    }

    public Algorithm create(Integer timeQuantum, Double initPower, Double powerThreshold) {
        if (this.name.equals(AlgorithmName.FCFS)) {
            return new FirstComeFirstService();
        }
        if (this.name.equals(AlgorithmName.RR)) {
            return new RoundRobin(timeQuantum);
        }
        if (this.name.equals(AlgorithmName.HRRN)) {
            return new HighResponseRatioNext();
        }
        if (this.name.equals(AlgorithmName.SPN)) {
            return new ShortestProcessNext();
        }
        if (this.name.equals(AlgorithmName.SRTN)) {
            return new ShortestRemainingTimeNext();
        }
        if (this.name.equals(AlgorithmName.CUSTOM)) {
            return new CustomSatellite(timeQuantum, initPower, powerThreshold);
        }

        throw new RuntimeException("알맞는 알고리즘이 없음");
    }
}
