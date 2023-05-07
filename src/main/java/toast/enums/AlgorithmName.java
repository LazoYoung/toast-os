package toast.enums;

import java.util.Arrays;
import toast.algorithm.CustomSatellite;
import toast.algorithm.FirstComeFirstService;
import toast.algorithm.HighResponseRatioNext;
import toast.algorithm.RoundRobin;
import toast.algorithm.ShortestProcessNext;
import toast.algorithm.ShortestRemainingTimeNext;
import toast.api.Algorithm;

public enum AlgorithmName {
    FCFS(FirstComeFirstService.class),
    RR(RoundRobin.class),
    HRRN(HighResponseRatioNext.class),
    SPN(ShortestProcessNext.class),
    SRTN(ShortestRemainingTimeNext.class),
    CUSTOM(CustomSatellite.class),
    ;

    private final Class<? extends Algorithm> algorithmClass;

    AlgorithmName(Class<? extends Algorithm> algorithmClass) {
        this.algorithmClass = algorithmClass;
    }

    public Class<? extends Algorithm> getAlgorithmClass() {
        return algorithmClass;
    }

//    public static AlgorithmName  mappingFor(Algorithm algorithm) {
//        return Arrays.stream(values())
//                .filter(algorithmName -> algorithmName.getAlgorithmClass().equals(algorithm.getClass()))
//                .findAny()
//                .orElseThrow(() -> new RuntimeException("일치하는 알고리즘이 없습니다."));
//    }
}
