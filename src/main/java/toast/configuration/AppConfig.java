package toast.configuration;

import toast.algorithm.Algorithm;
import toast.algorithm.ShortestProcessNext;
import toast.api.Core;

public class AppConfig {

    public Core primaryCore() {
        return Core.PERFORMANCE;
    }

    public Algorithm algorithm() {
        return new ShortestProcessNext();
    }
}
