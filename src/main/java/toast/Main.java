package toast;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("# of processor: ");
        int processor = scanner.nextInt();

        System.out.print("# of P core: ");
        int pCore = scanner.nextInt();

        System.out.print("# of process: ");
        int process = scanner.nextInt();

        System.out.printf("Arrival time (%d): ", process);
        int[] arrival = new int[process];

        for (int i = 0; i < process; i++) {
            arrival[i] = scanner.nextInt();
        }

        System.out.printf("Burst time (%d): ", process);
        int[] burst = new int[process];
        List<ConcreteProcess> processList = new ArrayList<>();

        for (int i = 0; i < process; i++) {
            burst[i] = scanner.nextInt();
            processList.add(new ConcreteProcess(arrival[i], burst[i]));
        }

        System.out.print("Time quantum for RR: ");
        int quantum = scanner.nextInt();

        Scheduler scheduler = new Scheduler();
        RoundRobin roundRobin = new RoundRobin(scheduler);
        scheduler.start(roundRobin, processList);
    }

}
