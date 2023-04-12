package toast.configuration;

import toast.algorithm.Algorithm;
import toast.algorithm.HighResponseRatioNext;
import toast.api.Core;

public class AppConfig {

    public Core primaryCore() {
        return Core.PERFORMANCE;
    }

    public Algorithm algorithm(int timeQuantum) {
        return new HighResponseRatioNext();
    }
}
