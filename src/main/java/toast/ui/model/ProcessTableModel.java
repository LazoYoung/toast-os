package toast.ui.model;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import toast.api.Process;

public class ProcessTableModel {
    private final SimpleIntegerProperty pid;
    private final SimpleIntegerProperty arrivalTime;
    private final SimpleIntegerProperty workload;
    private final SimpleIntegerProperty waitingTime;
    private final SimpleIntegerProperty turnaroundTime;
    private final SimpleDoubleProperty normalizedTT;

    public ProcessTableModel(Process process) {
        this.pid = new SimpleIntegerProperty(process.getId());
        this.arrivalTime = new SimpleIntegerProperty(process.getArrivalTime());
        this.workload = new SimpleIntegerProperty(process.getWorkload());
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

    public ObservableValue<Integer> getWorkload() {
        return new ReadOnlyObjectWrapper<>(workload.get());
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

    public void setPid(int pid) {
        this.pid.set(pid);
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime.set(arrivalTime);
    }

    public void setWorkload(int workload) {
        this.workload.set(workload);
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime.set(waitingTime);
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime.set(turnaroundTime);
    }

    public void setNormalizedTT(double normalizedTT) {
        this.normalizedTT.set(normalizedTT);
    }
}
