package toast.persistence.mapper;

import javafx.collections.ObservableList;
import toast.api.Core;
import toast.enums.AlgorithmName;
import toast.impl.ToastProcess;
import toast.impl.ToastProcessor;
import toast.persistence.domain.SchedulerConfig;
import toast.ui.controller.SettingsController.TempProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetUpMapper {

    private static AlgorithmName algorithmName;
    private static Integer timeQuantumValue;
    private static Double initPowerValue;
    private static Double powerThresholdValue;

    private static List<ToastProcessor> processors;
    private static List<ToastProcess> processes;


    private static AlgorithmName algorithmNameDraft;
    private static String timeQuantumValueDraft;
    private static String initPowerDraft;
    private static String powerThresholdDraft;

    private static Integer core1IdxDraft;
    private static Integer core2IdxDraft;
    private static Integer core3IdxDraft;
    private static Integer core4IdxDraft;

    private static ObservableList<TempProcess> processesDraft;

    public static AlgorithmName getAlgorithmName() {
        return algorithmNameDraft;
    }

    public static void setAlgorithmName(AlgorithmName algorithmName) {
        SetUpMapper.algorithmNameDraft = algorithmName;
        if (algorithmName == null) {
            throw new RuntimeException("알고리즘을 종류를 선택해주세요.");
        }
        System.out.println("algorithmName = " + algorithmName.name());
        SetUpMapper.algorithmName = algorithmName;
    }

    public static String getTimeQuantumValue() {
        return timeQuantumValueDraft;
    }

    public static void setTimeQuantumValue(String timeQuantum) {
        SetUpMapper.timeQuantumValueDraft = timeQuantum;
        try {
            if (timeQuantum == null) {
                throw new RuntimeException();
            }
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

    public static String getInitPowerValue() {
        return initPowerDraft;
    }

    public static void setInitPowerValue(String initPower) {
        SetUpMapper.initPowerDraft = initPower;
        try {
            if (initPower == null) {
                throw new RuntimeException();
            }
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

    public static String getPowerThresholdValue() {
        return powerThresholdDraft;
    }

    public static void setPowerThresholdValue(String powerThreshold) {
        powerThresholdDraft = powerThreshold;

        try {
            if (powerThreshold == null) {
                throw new RuntimeException();
            }
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

    public static Integer getCore1Idx() {
        return core1IdxDraft;
    }

    public static Integer getCore2Idx() {
        return core2IdxDraft;
    }

    public static Integer getCore3Idx() {
        return core3IdxDraft;
    }

    public static Integer getCore4Idx() {
        return core4IdxDraft;
    }

    public static void setProcessors(int core1, int core2, int core3, int core4) {
        core1IdxDraft = core1;
        core2IdxDraft = core2;
        core3IdxDraft = core3;
        core4IdxDraft = core4;

        List<ToastProcessor> cores = new ArrayList<>();

        cores.add(new ToastProcessor(1, Core.mappingFor(core1), core1 != 0));
        System.out.println("core1Value = " + (core1 == 0 ? "OFF" : core1));
        cores.add(new ToastProcessor(2, Core.mappingFor(core2), core2 != 0));
        System.out.println("core2Value = " + (core2 == 0 ? "OFF" : core2));
        cores.add(new ToastProcessor(3, Core.mappingFor(core3), core3 != 0));
        System.out.println("core3Value = " + (core3 == 0 ? "OFF" : core3));
        cores.add(new ToastProcessor(4, Core.mappingFor(core4), core4 != 0));
        System.out.println("core4Value = " + (core4 == 0 ? "OFF" : core4));

        if (cores.stream().filter(ToastProcessor::isActive).findAny().isEmpty()) {
            throw new RuntimeException("프로세서를 적어도 한 개 이상을 켜야 합니다.");
        }

        SetUpMapper.processors = cores;
    }

    public static ObservableList<TempProcess> getProcesses() {
        return processesDraft;
    }

    public static void setProcesses(ObservableList<TempProcess> data) {
        processesDraft = data;

        List<ToastProcess> processes = data.stream().map(TempProcess::toToastProcess).collect(Collectors.toList());
        if (processes.isEmpty()) {
            throw new RuntimeException("프로세스를 적어도 하나 이상 입력해주세요.");
        }
        SetUpMapper.processes = processes;
    }

    public static SchedulerConfig mapToSchedulerConfig() throws RuntimeException {
        var config = new SchedulerConfig(Core.PERFORMANCE, timeQuantumValue, initPowerValue, powerThresholdValue);
        config.setAlgorithm(algorithmName);
        config.setProcessList(processes);
        config.setProcessorList(processors);
        return config;
    }
}
