package toast.configuration;

import java.util.List;
import toast.algorithm.Algorithm;
import toast.algorithm.ShortestProcessNext;
import toast.api.Core;
import toast.impl.ToastProcess;
import toast.impl.ToastProcessor;
import toast.impl.ToastScheduler;

public class AppConfig {

    public static Core primaryCore() {
        return Core.PERFORMANCE;
    }

    public static Algorithm algorithm() {
        return new ShortestProcessNext();
    }

    public static ToastScheduler scheduler(List<ToastProcessor> coreList, List<ToastProcess> processList) {
        return new ToastScheduler(coreList, processList, primaryCore(), algorithm());
    }
}
