package toast.ui.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
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
import toast.enums.AlgorithmName;

public class SettingController implements Initializable {
    @FXML
    private ChoiceBox<AlgorithmName> algorithmNameChoiceBox;

//    @FXML
//    private ChoiceBox<AlgorithmName> processor1;
//
//    @FXML
//    private ChoiceBox<AlgorithmName> processor1;
//
//    @FXML
//    private ChoiceBox<AlgorithmName> processor1;
//
//    @FXML
//    private ChoiceBox<AlgorithmName> processor1;

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

        table.setEditable(true);
        data.add(new TempProcess(33,4,5,"T"));
        table.setItems(data);



        processIdColumn.setCellValueFactory(new PropertyValueFactory<>("processId"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        workLoadColumn.setCellValueFactory(new PropertyValueFactory<>("workLoad"));
        missionColumn.setCellValueFactory(new PropertyValueFactory<>("mission"));
//        table.getColumns().addAll(processIdColumn, arrivalTimeColumn, workLoadColumn, missionColumn);

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

    }

}
