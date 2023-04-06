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
        List<Process> processList = new ArrayList<>();

        for (int i = 0; i < process; i++) {
            burst[i] = scanner.nextInt();
            processList.add(new Process(arrival[i], burst[i]));
        }

        System.out.print("Time quantum for RR: ");
        int quantum = scanner.nextInt();

        RoundRobin rr = new RoundRobin(processList);
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            boolean rrComplete = false;

            @Override
            public void run() {
                if (!rrComplete) {
                    rrComplete = rr.run();
                }
            }
        }, 0L, 1000L);
    }

}
