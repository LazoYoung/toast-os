package toast.persistence.domain;

import java.util.HashMap;
import java.util.Map;

public class ProcessRecord {
    public enum Metric {
        ARRIVAL_TIME,
        BURST_TIME,
        WAITING_TIME,
        TURNAROUND_TIME,
        NORMALIZED_TT
    }

    private final Map<Metric, Number> metricMap = new HashMap<>();

    public Number getMetric(Metric metric) {
        return metricMap.get(metric);
    }

    public void setMetric(Metric metric, Number number) {
        metricMap.put(metric, number);
    }
}
