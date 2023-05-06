package toast;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import toast.api.Core;
import toast.configuration.AppConfig;
import toast.impl.ToastProcess;
import toast.impl.ToastProcessor;
import toast.impl.ToastScheduler;
import toast.ui.view.GanttChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        Scanner scanner = new Scanner(System.in);
        List<ToastProcessor> coreList = getCoreList(scanner);
        List<ToastProcess> processList = getProcessList(scanner);

        int timeQuantum = getTimeQuantum(scanner);
        double initPower = getInitPower(scanner);
        double powerThreshold = getPowerThreshold(scanner);
        System.out.println();
        scanner.close();

        ToastScheduler scheduler = scheduler(timeQuantum, initPower, powerThreshold, coreList, processList);
        scheduler.start();

        try {
//            Scene scene = getMainScene();
            Scene scene = new Scene(new GanttChart(scheduler));
            primaryStage.setTitle("Process Scheduling Simulator");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }

    private Scene getMainScene() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        return scene;
    }

    private static ToastScheduler scheduler(int timeQuantum, double initPower, double powerThreshold, List<ToastProcessor> coreList, List<ToastProcess> processList) {
        AppConfig appConfig = new AppConfig();

        return new ToastScheduler(coreList, processList, appConfig.primaryCore(), appConfig.algorithm(timeQuantum, initPower, powerThreshold));
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

        while (pCount-- > 0) {
            Core core = (pCore-- > 0) ? Core.PERFORMANCE : Core.EFFICIENCY;
            list.add(new ToastProcessor(core));
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
