package toast.api;

public interface Process {
    int getId();
    int getArrivalTime();
    int getWaitingTime();
    int getTurnaroundTime();
    double getNormalizedTurnaroundTime();
    int getWorkload();
    int getRemainingWorkload();
}
