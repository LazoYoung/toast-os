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
import javafx.fxml.Initializable;
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

public class SettingController implements Initializable {
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

    public void saveResult() {
        SetUpMapper.save(makeResult());
    }

    private SetUpResult makeResult() {
        try {

            AlgorithmName algorithmName = algorithmNameChoiceBox.getValue();
            System.out.println("algorithmName = " + algorithmName.name());

            int timeQuantumValue = Integer.parseInt(timeQuantum.getText());
            System.out.println("timeQuantumValue = " + timeQuantumValue);
            double initPowerValue = Double.parseDouble(initPower.getText());
            System.out.println("initPowerValue = " + initPowerValue);
            double powerThresholdValue = Double.parseDouble(powerThreshold.getText());
            System.out.println("powerThresholdValue = " + powerThresholdValue);

            Core core1Value = Core.mappingFor(core1.getIdx());
            System.out.println("core1Value = " + core1Value.getName());
            Core core2Value = Core.mappingFor(core2.getIdx());
            System.out.println("core2Value = " + core2Value.getName());
            Core core3Value = Core.mappingFor(core3.getIdx());
            System.out.println("core3Value = " + core3Value.getName());
            Core core4Value = Core.mappingFor(core4.getIdx());
            System.out.println("core4Value = " + core4Value.getName());

            List<ToastProcess> processes = data.stream().map(TempProcess::toToastProcess).collect(Collectors.toList());
            processes.forEach(toastProcess -> {
                System.out.println("toastProcess = " + toastProcess.getId());
            });


            return new SetUpResult(
                    algorithmName,
                    timeQuantumValue,
                    initPowerValue,
                    powerThresholdValue,
                    core1Value,
                    core2Value,
                    core3Value,
                    core4Value,
                    processes
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    }

}
