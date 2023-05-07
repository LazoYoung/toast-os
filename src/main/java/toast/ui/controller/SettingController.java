package toast.ui.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import toast.api.Core;
import toast.enums.AlgorithmName;
import toast.impl.ToastProcess;
import toast.persistence.mapper.SetUpMapper;
import toast.ui.view.CoreProcessorButton;

public class SettingController extends PageController {
    @FXML
    private ChoiceBox<AlgorithmName> algorithmNameChoiceBox;
    @FXML
    private TextField timeQuantum;
    @FXML
    private TextField initPower;
    @FXML
    private TextField powerThreshold;

    @FXML
    private CoreProcessorButton core1;
    @FXML
    private CoreProcessorButton core2;
    @FXML
    private CoreProcessorButton core3;
    @FXML
    private CoreProcessorButton core4;

    @FXML
    private TableView<TempProcess> table;

    ObservableList<TempProcess> data = FXCollections.observableArrayList();

    public ObservableList<TempProcess> getData() {
        return data;
    }

    @FXML
    private TableColumn<TempProcess, SimpleIntegerProperty> processIdColumn;
    @FXML
    private TableColumn<TempProcess, SimpleIntegerProperty> arrivalTimeColumn;
    @FXML
    private TableColumn<TempProcess, SimpleIntegerProperty> workLoadColumn;
    @FXML
    private TableColumn<TempProcess, SimpleStringProperty> missionColumn;

    @FXML
    private TextField processId;
    @FXML
    private TextField arrivalTime;
    @FXML
    private TextField workLoad;
    @FXML
    private TextField mission;

    @FXML
    private MFXButton add;
    @FXML
    private MFXButton remove;
    @FXML
    private MFXButton clear;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithmNameChoiceBox.getItems().addAll(AlgorithmName.values());
        processSetting();
    }

    @Override
    void init() {
        AlgorithmName algorithmName = SetUpMapper.getAlgorithmName();
        if (algorithmName != null) {
            algorithmNameChoiceBox.setValue(algorithmName);
        }

        String timeQuantumValue = SetUpMapper.getTimeQuantumValue();
        if (timeQuantumValue != null) {
            timeQuantum.setText(timeQuantumValue);
        }

        String initPowerValue = SetUpMapper.getInitPowerValue();
        if (initPowerValue != null) {
            initPower.setText(initPowerValue);
        }

        String powerThresholdValue = SetUpMapper.getPowerThresholdValue();
        if (powerThresholdValue != null) {
            powerThreshold.setText(powerThresholdValue);
        }

        if (SetUpMapper.getCore1Idx() != null) {
            core1.setIdx(SetUpMapper.getCore1Idx());
        }
        if (SetUpMapper.getCore2Idx() != null) {
            core2.setIdx(SetUpMapper.getCore2Idx());
        }
        if (SetUpMapper.getCore3Idx() != null) {
            core3.setIdx(SetUpMapper.getCore3Idx());
        }
        if (SetUpMapper.getCore4Idx() != null) {
            core4.setIdx(SetUpMapper.getCore4Idx());
        }

        var processes = SetUpMapper.getProcesses();
        if (processes != null) {
            data = FXCollections.observableArrayList(processes);
            table.setItems(data);
        }

    }

    @Override
    void exit() {
        saveResultDraft();
    }

    private static int getIdx(Core core) {
        return (core == null) ? 0 : core.getIdx();
    }

    public void saveResultDraft() {
        SetUpMapper.setAlgorithmName(algorithmNameChoiceBox.getValue());

        SetUpMapper.setTimeQuantumValue(timeQuantum.getText());
        SetUpMapper.setInitPowerValue(initPower.getText());
        SetUpMapper.setPowerThresholdValue(powerThreshold.getText());

        SetUpMapper.setAlgorithmName(algorithmNameChoiceBox.getValue());
        SetUpMapper.setAlgorithmName(algorithmNameChoiceBox.getValue());

        SetUpMapper.setProcessors(core1.getIdx(), core2.getIdx(), core3.getIdx(), core4.getIdx());

        SetUpMapper.setProcesses(data);
    }

    public void saveResult() {
        try {
            SetUpMapper.setIsDone(true);
            saveResultDraft();

        } catch (Exception e) {
            e.printStackTrace();

            SetUpMapper.setIsDone(false);
        }
    }

    private void processSetting() {
        tableSetting();
        buttonSetting();
    }

    private void tableSetting() {
        table.setEditable(true);
//        data.add(new TempProcess(33, 4, 5, "T"));
        table.setItems(data);

        processIdColumn.setCellValueFactory(new PropertyValueFactory<>("processId"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        workLoadColumn.setCellValueFactory(new PropertyValueFactory<>("workLoad"));
        missionColumn.setCellValueFactory(new PropertyValueFactory<>("mission"));
//        table.getColumns().addAll(processIdColumn, arrivalTimeColumn, workLoadColumn, missionColumn);
    }

    private void buttonSetting() {
        add.setOnAction(event -> data.add(
                new TempProcess(Integer.parseInt(processId.getText()), Integer.parseInt(arrivalTime.getText()),
                        Integer.parseInt(workLoad.getText()), mission.getText())));

        remove.setOnAction(event -> {
            var item = table.getSelectionModel().getSelectedItem();
            table.getItems().remove(item);
        });

        clear.setOnAction(event -> {
            table.getItems().clear();
        });
    }

    public static class TempProcess {
        public final SimpleIntegerProperty processId;
        public final SimpleIntegerProperty arrivalTime;
        public final SimpleIntegerProperty workLoad;
        public final SimpleStringProperty mission;

        public TempProcess(int processId, int arrivalTime, int workLoad, String mission) {
            this.processId = new SimpleIntegerProperty(processId);
            this.arrivalTime = new SimpleIntegerProperty(arrivalTime);
            this.workLoad = new SimpleIntegerProperty(workLoad);
            this.mission = new SimpleStringProperty(mission);
        }

        public int getProcessId() {
            return processId.get();
        }

        public void setProcessId(int processId) {
            this.processId.set(processId);
        }

        public int getArrivalTime() {
            return arrivalTime.get();
        }

        public void setArrivalTime(int arrivalTime) {
            this.arrivalTime.set(arrivalTime);
        }

        public int getWorkLoad() {
            return workLoad.get();
        }

        public void setWorkLoad(int workLoad) {
            this.workLoad.set(workLoad);
        }

        public String getMission() {
            return mission.get();
        }

        public void setMission(String mission) {
            this.mission.set(mission);
        }

        SimpleIntegerProperty processIdProperty() {
            return processId;
        }

        SimpleIntegerProperty arrivalTimeProperty() {
            return arrivalTime;
        }

        SimpleIntegerProperty workLoadProperty() {
            return workLoad;
        }

        SimpleStringProperty missionProperty() {
            return mission;
        }

        public ToastProcess toToastProcess() {
            return new ToastProcess(getProcessId(), getArrivalTime(), getWorkLoad(), getMission().equals("T"));
        }

        public static TempProcess create(ToastProcess toastProcess) {
            return new TempProcess(toastProcess.getId(), toastProcess.getArrivalTime(), toastProcess.getWorkload(),
                    toastProcess.isMission() ? "T" : "F");
        }

    }

}
