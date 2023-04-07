package toast.api;

public interface Process {
    int getId();
    int getArrivalTime();
    int getWaitingTime();
    int getTurnaroundTime();
    int getNormalizedTurnaroundTime();
    int getWorkload();
    int getRemainingWorkload();
}
