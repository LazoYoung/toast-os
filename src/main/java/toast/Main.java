package toast;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Core> coreList = getCoreList();
        List<ConcreteProcess> processList = getProcessList();
        int timeQuantum = getTimeQuantum();

        // Algorithm you want to simulate
        ShortestProcessNext spn = new ShortestProcessNext();

        Scheduler scheduler = new Scheduler(spn);
        scheduler.start(processList);
    }

    private static List<ConcreteProcess> getProcessList() {
        List<ConcreteProcess> list = new ArrayList<>();

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("# of process: ");
            int process = scanner.nextInt();

            System.out.printf("Arrival time (%d): ", process);
            int[] arrival = new int[process];

            for (int i = 0; i < process; i++) {
                arrival[i] = scanner.nextInt();
            }

            System.out.printf("Burst time (%d): ", process);
            int[] burst = new int[process];

            for (int i = 0; i < process; i++) {
                burst[i] = scanner.nextInt();
                list.add(new ConcreteProcess(arrival[i], burst[i]));
            }
        }

        return list;
    }

    private static List<Core> getCoreList() {
        List<Core> list = new ArrayList<>();

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("# of processor: ");
            int processor = scanner.nextInt();

            System.out.print("# of P core: ");
            int pCore = scanner.nextInt();

            while (processor-- > 0) {
                Core core = (pCore-- > 0) ? Core.PERFORMANCE : Core.EFFICIENCY;
                list.add(core);
            }
        }

        return list;
    }

    private static int getTimeQuantum() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Time quantum for RR: ");
            return scanner.nextInt();
        }
    }

}
