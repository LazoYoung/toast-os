package toast;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import toast.api.Core;
import toast.enums.AlgorithmName;
import toast.impl.ToastProcess;
import toast.impl.ToastProcessor;
import toast.impl.ToastScheduler;
import toast.persistence.domain.SchedulerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ToastApp extends Application {
    public static Stage splashStage = null;

    @Override
    public void start(Stage primaryStage) {
        splashStage = primaryStage;
        try {
            Parent splash = FXMLLoader.load(getClass().getResource("splash.fxml"));
            Scene splashScene = new Scene(splash);

            primaryStage.setScene(splashScene);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void start(String[] args) {
        Application.launch(args);
//        startSchedulerWithTerminal();
    }

    private static void startSchedulerWithTerminal() {
        SchedulerConfig config = makeInput();
        ToastScheduler.getInstance().setup(config).start();
    }

    private static SchedulerConfig makeInput() {
        Scanner scanner = new Scanner(System.in);
        List<ToastProcessor> processorList = getCoreList(scanner);
        List<ToastProcess> processList = getProcessList(scanner);

        int timeQuantum = getTimeQuantum(scanner);
        double initPower = getInitPower(scanner);
        double powerThreshold = getPowerThreshold(scanner);
        System.out.println();
        scanner.close();

        var config = new SchedulerConfig(Core.PERFORMANCE, timeQuantum, initPower, powerThreshold);
        config.setAlgorithm(AlgorithmName.CUSTOM);
        config.setProcessorList(processorList);
        config.setProcessList(processList);
        return config;
    }

    private static List<ToastProcess> getProcessList(Scanner scanner) {
        List<ToastProcess> list = new ArrayList<>();
        System.out.print("# of process: ");
        int process = scanner.nextInt();

        System.out.printf("│ Arrival time (%d): ", process);
        int[] arrival = new int[process];

        for (int i = 0; i < process; i++) {
            arrival[i] = scanner.nextInt();
        }

        System.out.printf("│ Burst time (%d): ", process);
        int[] burst = new int[process];

        for (int i = 0; i < process; i++) {
            burst[i] = scanner.nextInt();
        }

        System.out.printf("│ Mission (%d): ", process);
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

        for (int id = 1; id <= 4; id++) {
            Core core = (pCore-- < 1) ? Core.EFFICIENCY : Core.PERFORMANCE;
            boolean active = (pCount-- > 0);
            ToastProcessor processor = new ToastProcessor(id, core, active);
            list.add(processor);
        }
        return list;
    }

    private static int getTimeQuantum(Scanner scanner) {
        System.out.print("│ Time quantum: ");
        return scanner.nextInt();
    }

    private static double getInitPower(Scanner scanner) {
        System.out.print("│ Initial power: ");
        return scanner.nextDouble();
    }

    private static double getPowerThreshold(Scanner scanner) {
        System.out.print("│ Power threshold: ");
        return scanner.nextDouble();
    }
}
