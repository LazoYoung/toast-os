package toast;

import toast.api.Core;
import toast.configuration.AppConfig;
import toast.impl.ToastProcess;
import toast.impl.ToastProcessor;
import toast.impl.ToastScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<ToastProcessor> coreList = getCoreList(scanner);
        List<ToastProcess> processList = getProcessList(scanner);

        int timeQuantum = getTimeQuantum(scanner);
        double initPower = getInitPower(scanner);
        System.out.println();
        scanner.close();

        ToastScheduler scheduler = scheduler(timeQuantum, initPower, coreList, processList);
        scheduler.start();
    }

    private static ToastScheduler scheduler(int timeQuantum, double initPower, List<ToastProcessor> coreList, List<ToastProcess> processList) {
        AppConfig appConfig = new AppConfig();

        return new ToastScheduler(coreList, processList, appConfig.primaryCore(), appConfig.algorithm(timeQuantum, initPower));
    }

    private static List<ToastProcess> getProcessList(Scanner scanner) {
        List<ToastProcess> list = new ArrayList<>();
        System.out.print("# of process: ");
        int process = scanner.nextInt();

        System.out.printf("│Arrival time (%d): ", process);
        int[] arrival = new int[process];

        for (int i = 0; i < process; i++) {
            arrival[i] = scanner.nextInt();
        }

        System.out.printf("│Burst time (%d): ", process);
        int[] burst = new int[process];

        for (int i = 0; i < process; i++) {
            burst[i] = scanner.nextInt();
        }

        System.out.printf("│Mission (%d): ", process);
        boolean[] mission = new boolean[process];

        for (int i = 0; i < process; i++) {
            mission[i] = (scanner.nextInt() == 1);
            list.add(new ToastProcess(arrival[i], burst[i], mission[i]));
        }
        return list;
    }

    private static List<ToastProcessor> getCoreList(Scanner scanner) {
        List<ToastProcessor> list = new ArrayList<>();
        System.out.print("# of processor: ");
        int pCount = scanner.nextInt();

        System.out.print("# of P core: ");
        int pCore = scanner.nextInt();

        while (pCount-- > 0) {
            Core core = (pCore-- > 0) ? Core.PERFORMANCE : Core.EFFICIENCY;
            list.add(new ToastProcessor(core));
        }
        return list;
    }

    private static int getTimeQuantum(Scanner scanner) {
        System.out.print("│Time quantum for RR: ");
        return scanner.nextInt();
    }

    private static double getInitPower(Scanner scanner) {
        System.out.print("│Init power for CustomAlgorithm: ");
        return scanner.nextDouble();
    }

}
