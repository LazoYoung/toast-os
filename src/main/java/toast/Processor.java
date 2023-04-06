package toast;

public interface Processor {
    Process getRunningProcess();
    int getPowerConsumed();
    void dispatch(Process process);
    void preempt();
}
