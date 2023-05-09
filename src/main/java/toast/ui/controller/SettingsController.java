package toast.ui.controller;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import toast.api.Core;
import toast.enums.AlgorithmName;
import toast.impl.ToastProcess;
import toast.impl.ToastScheduler;
import toast.persistence.domain.SchedulerConfig;
import toast.persistence.mapper.SetUpMapper;
import toast.ui.view.CoreProcessorButton;

public class SettingsController extends PageController {
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
    private MFXButton addButton;
    @FXML
    private MFXButton removeButton;
    @FXML
    private MFXButton clearButton;
    @FXML
    private MFXButton saveButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithmNameChoiceBox.getItems().addAll(AlgorithmName.values());
        initTable();
        initButtons();
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
        try {
            saveResultDraft();
        } catch (Exception ignored) {
            // Ignoring exception is sign of bad design
        }
    }

    private static int getIdx(Core core) {
        return (core == null) ? 0 : core.getIdx();
    }

    public void saveResultDraft() {
        SetUpMapper.setAlgorithmName(algorithmNameChoiceBox.getValue());

        switch (SetUpMapper.getAlgorithmName()) {
            case RR -> SetUpMapper.setTimeQuantumValue(timeQuantum.getText());
            case CUSTOM -> {
                SetUpMapper.setTimeQuantumValue(timeQuantum.getText());
                SetUpMapper.setInitPowerValue(initPower.getText());
                SetUpMapper.setPowerThresholdValue(powerThreshold.getText());
            }
        }

        SetUpMapper.setProcessors(core1.getIdx(), core2.getIdx(), core3.getIdx(), core4.getIdx());
        SetUpMapper.setProcesses(data);
    }

    private void initButtons() {
        addButton.setOnAction(this::onProcessAdd);
        removeButton.setOnAction(this::onProcessRemove);
        clearButton.setOnAction(this::onProcessClear);
        saveButton.setOnAction(this::onSave);
    }

    private void initTable() {
        table.setEditable(true);
        table.setItems(data);

        processIdColumn.setCellValueFactory(new PropertyValueFactory<>("processId"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        workLoadColumn.setCellValueFactory(new PropertyValueFactory<>("workLoad"));
        missionColumn.setCellValueFactory(new PropertyValueFactory<>("mission"));

//        table.getColumns().addAll(processIdColumn, arrivalTimeColumn, workLoadColumn, missionColumn);
    }

    private void onSave(ActionEvent event) {
        try {
            saveResultDraft();
            SchedulerConfig config = SetUpMapper.mapToSchedulerConfig();
            ToastScheduler.getInstance().setup(config);

            String message = "설정이 저장되었습니다.";
            Alert alert = new Alert(INFORMATION, message, ButtonType.OK);
            alert.setHeaderText("Success!");
            Platform.runLater(alert::showAndWait);
        } catch (Exception e) {
            Alert alert = new Alert(ERROR, e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed to save...");
            Platform.runLater(alert::showAndWait);
        }
    }

    private void onProcessRemove(ActionEvent event) {
        var item = table.getSelectionModel().getSelectedItem();
        table.getItems().remove(item);
    }

    private void onProcessAdd(ActionEvent event) {
        try {
            var process = new TempProcess(
                    getProcessId(),
                    getArrivalTime(),
                    getWorkLoad(),
                    mission.getText()
            );
            data.add(process);
            clearProcessInput();
        } catch (Exception e) {
            Alert alert = new Alert(ERROR, e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed to add...");
            Platform.runLater(alert::showAndWait);
        }
    }

    private int getWorkLoad() {
        try {
            int ret = Integer.parseInt(workLoad.getText());
            if (ret < 0) {
                throw new IllegalArgumentException("workLoad는 음수일 수 없습니다.");
            }
            return ret;
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("workLoad는 정수형태로 입력해주세요.");
        }
    }

    private int getArrivalTime() {
        try {
            int ret = Integer.parseInt(arrivalTime.getText());
            if (ret < 0) {
                throw new IllegalArgumentException("arrivalTime는 음수일 수 없습니다.");
            }
            return ret;
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("arrivalTime는 정수형태로 입력해주세요.");
        }
    }

    private int getProcessId() {
        try {
            int ret = Integer.parseInt(processId.getText());
            if (ret < 0) {
                throw new IllegalArgumentException("processId는 음수일 수 없습니다.");
            }
            return ret;
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("processId는 정수형태로 입력해주세요.");
        }

    }

    private void clearProcessInput() {
        processId.clear();
        arrivalTime.clear();
        workLoad.clear();
        mission.clear();
    }

    private void onProcessClear(ActionEvent event) {
        table.getItems().clear();
    }

    public static class TempProcess {
        public static final String TRUE = "T";
        public static final String FALSE = "F";
        public final SimpleIntegerProperty processId;
        public final SimpleIntegerProperty arrivalTime;
        public final SimpleIntegerProperty workLoad;
        public final SimpleStringProperty mission;

        public TempProcess(int processId, int arrivalTime, int workLoad, String mission) {
            this.processId = new SimpleIntegerProperty(processId);
            this.arrivalTime = new SimpleIntegerProperty(arrivalTime);
            this.workLoad = new SimpleIntegerProperty(workLoad);
            this.mission = new SimpleStringProperty(isMission(mission) ? TRUE : FALSE);
        }

        private static boolean isValidMission(String mission) {
            return mission.equals(TRUE) || mission.equals(FALSE);
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
            return new ToastProcess(getProcessId(), getArrivalTime(), getWorkLoad(), isMission(getMission()));
        }

        private static boolean isMission(String mission1) {
            return TRUE.equals(mission1);
        }

    }

}
