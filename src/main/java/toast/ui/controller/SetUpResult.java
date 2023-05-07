package toast.ui.controller;

import java.util.ArrayList;
import java.util.List;
import toast.algorithm.AlgorithmFactory;
import toast.api.Algorithm;
import toast.api.Core;
import toast.enums.AlgorithmName;
import toast.impl.ToastProcess;
import toast.impl.ToastProcessor;
import toast.impl.ToastScheduler;
import toast.persistence.domain.AppConfig;

public class SetUpResult {

    private final Algorithm algorithm;

    private final ToastScheduler scheduler;

    public SetUpResult(AlgorithmName algorithmName, Integer timeQuantum, Double initPower, Double powerThreshold,
                       Core core1, Core core2, Core core3, Core core4, List<ToastProcess> processes) {
        Algorithm algorithm = new AlgorithmFactory(algorithmName).create(timeQuantum, initPower, powerThreshold);

        List<ToastProcessor> cores = new ArrayList<>();

        if(core1 != null) cores.add(new ToastProcessor(core1, true));
        if(core2 != null) cores.add(new ToastProcessor(core2, true));
        if(core3 != null) cores.add(new ToastProcessor(core3, true));
        if(core4 != null) cores.add(new ToastProcessor(core4, true));

        this.algorithm = algorithm;
        this.scheduler = scheduler(algorithm, cores, processes);
    }

    private static ToastScheduler scheduler(Algorithm algorithm, List<ToastProcessor> coreList,
                                            List<ToastProcess> processList) {
        return new ToastScheduler(coreList, processList, new AppConfig().primaryCore(),
                algorithm);
    }
}
