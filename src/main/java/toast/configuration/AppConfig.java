package toast.configuration;

import toast.algorithm.Algorithm;
import toast.algorithm.FirstComeFirstService;
import toast.api.Core;

public class AppConfig {

    public Core primaryCore() {
        return Core.PERFORMANCE;
    }

    public Algorithm algorithm(int timeQuantum, double initPower) {
        return new FirstComeFirstService();
    }
}
