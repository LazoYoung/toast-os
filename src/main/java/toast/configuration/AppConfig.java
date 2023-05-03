package toast.configuration;

import toast.algorithm.Algorithm;
import toast.algorithm.*;
import toast.api.Core;

public class AppConfig {

    public Core primaryCore() {
        return Core.PERFORMANCE;
    }

    public Algorithm algorithm(int timeQuantum, double initPower, double powerThreshold) {
        return new CustomSatellite(timeQuantum, initPower, powerThreshold);
    }
}
