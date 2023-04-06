package toast;

public interface Processor {
    Process getRunningProcess();
    void dispatch(Process process);
}
