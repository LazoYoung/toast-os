package toast.persistence.mapper;

import java.util.ArrayList;
import java.util.List;
import toast.algorithm.AlgorithmFactory;
import toast.api.Algorithm;
import toast.api.Core;
import toast.enums.AlgorithmName;
import toast.impl.ToastProcess;
import toast.impl.ToastProcessor;
import toast.impl.ToastScheduler;
import toast.persistence.domain.AppConfig;

public class SetUpMapper {

    private static Boolean isDone;

    private static AlgorithmName algorithmName;
    private static Integer timeQuantumValue;
    private static Double initPowerValue;
    private static Double powerThresholdValue;

    private static List<ToastProcessor> processors;
    private static List<ToastProcess> processes;

    public static Boolean getIsDone() {
        return isDone;
    }

    public static void setIsDone(boolean isDone) {
        SetUpMapper.isDone = isDone;
    }

    public static AlgorithmName getAlgorithmName() {
        return algorithmName;
    }

    public static void setAlgorithmName(AlgorithmName algorithmName) {
        if (algorithmName == null) {
            throw new RuntimeException("알고리즘을 종류를 선택해주세요.");
        }
        System.out.println("algorithmName = " + algorithmName.name());
        SetUpMapper.algorithmName = algorithmName;
    }

    public static Integer getTimeQuantumValue() {
        return timeQuantumValue;
    }

    public static void setTimeQuantumValue(String timeQuantum) {
        try {
            int timeQuantumValue = Integer.parseInt(timeQuantum);
            if (timeQuantumValue <= 0) {
                throw new RuntimeException();
            }
            System.out.println("timeQuantumValue = " + timeQuantumValue);

            SetUpMapper.timeQuantumValue = timeQuantumValue;
        } catch (Exception exception) {
            throw new RuntimeException("타임 퀀텀을 1이상의 정수형태로 입력해주세요.");
        }
    }

    public static Double getInitPowerValue() {
        return initPowerValue;
    }

    public static void setInitPowerValue(String initPower) {
        try {
            Double initPowerValue = Double.parseDouble(initPower);
            if (initPowerValue <= 0) {
                throw new RuntimeException();
            }
            System.out.println("initPowerValue = " + initPowerValue);

            SetUpMapper.initPowerValue = initPowerValue;
        } catch (Exception exception) {
            throw new RuntimeException("시작 전력을 0보다 큰 실수로 입력해주세요.");
        }
    }

    public static Double getPowerThresholdValue() {
        return powerThresholdValue;
    }

    public static void setPowerThresholdValue(String powerThreshold) {
        try {
            Double powerThresholdValue = Double.parseDouble(powerThreshold);
            if (powerThresholdValue < 0 || powerThresholdValue >= 1) {
                throw new RuntimeException();
            }
            System.out.println("powerThresholdValue = " + powerThresholdValue);

            SetUpMapper.powerThresholdValue = powerThresholdValue;
        } catch (Exception exception) {
            throw new RuntimeException("임계치를 0 ~ 1 사이 실수로 입력해주세요.");
        }
    }

    public static List<ToastProcessor> getProcessors() {
        return processors;
    }

    public static void setProcessors(Core core1, Core core2, Core core3, Core core4) {
        List<ToastProcessor> cores = new ArrayList<>();

        cores.add(new ToastProcessor(core1, core1 != null));
        System.out.println("core1Value = " + (core1 == null ? "NULL" : core1.getName()));
        cores.add(new ToastProcessor(core2, core2 != null));
        System.out.println("core2Value = " + (core2 == null ? "NULL" : core2.getName()));
        cores.add(new ToastProcessor(core3, core3 != null));
        System.out.println("core3Value = " + (core3 == null ? "NULL" : core3.getName()));
        cores.add(new ToastProcessor(core4, core4 != null));
        System.out.println("core4Value = " + (core4 == null ? "NULL" : core4.getName()));

        if (cores.stream().filter(ToastProcessor::isActive).findAny().isEmpty()) {
            throw new RuntimeException("프로세서를 적어도 한 개 이상을 켜야 합니다.");
        }

        SetUpMapper.processors = cores;
    }

    public static List<ToastProcess> getProcesses() {
        return processes;
    }

    public static void setProcesses(List<ToastProcess> processes) {
        if (processes == null || processes.isEmpty()) {
            throw new RuntimeException("프로세스를 적어도 하나 이상 입력해주세요.");
        }

        SetUpMapper.processes = processes;
    }

    public static Algorithm getAlgorithm() {
        if (!getIsDone()) {
            throw new RuntimeException("세팅이 완료되지 않았습니다.");
        }
        return new AlgorithmFactory(getAlgorithmName()).create(getTimeQuantumValue(), getInitPowerValue(),
                getPowerThresholdValue());
    }

    public static ToastScheduler getToastScheduler() {
        if (!getIsDone()) {
            throw new RuntimeException("세팅이 완료되지 않았습니다.");
        }
        return new ToastScheduler(processors, processes, new AppConfig().primaryCore(),
                getAlgorithm());
    }
}
