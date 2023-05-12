package toast.ui.model;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import toast.api.Process;

public class ProcessTableModel {
    private final SimpleIntegerProperty pid;
    private final SimpleIntegerProperty arrivalTime;
    private final SimpleIntegerProperty burstTime;
    private final SimpleIntegerProperty waitingTime;
    private final SimpleIntegerProperty turnaroundTime;
    private final SimpleDoubleProperty normalizedTT;

    public ProcessTableModel(Process process) {
        this.pid = new SimpleIntegerProperty(process.getId());
        this.arrivalTime = new SimpleIntegerProperty(process.getArrivalTime());
        this.burstTime = new SimpleIntegerProperty(process.getBurstTime());
        this.waitingTime = new SimpleIntegerProperty(process.getWaitingTime());
        this.turnaroundTime = new SimpleIntegerProperty(process.getTurnaroundTime());
        this.normalizedTT = new SimpleDoubleProperty(process.getNormalizedTurnaroundTime());
    }

    public ObservableValue<Integer> getPid() {
        return new ReadOnlyObjectWrapper<>(pid.get());
    }

    public ObservableValue<Integer> getArrivalTime() {
        return new ReadOnlyObjectWrapper<>(arrivalTime.get());
    }

    public ObservableValue<Integer> getBurstTime() {
        return new ReadOnlyObjectWrapper<>(burstTime.get());
    }

    public ObservableValue<Integer> getWaitingTime() {
        return new ReadOnlyObjectWrapper<>(waitingTime.get());
    }
    public ObservableValue<Integer> getTurnaroundTime() {
        return new ReadOnlyObjectWrapper<>(turnaroundTime.get());
    }

    public ObservableValue<Double> getNormalizedTT() {
        double value = Math.round(normalizedTT.get() * 100) / 100.0;
        return new ReadOnlyObjectWrapper<>(value);
    }
}
