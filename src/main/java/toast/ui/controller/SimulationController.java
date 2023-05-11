package toast.ui.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import toast.api.Process;
import toast.event.ToastEvent;
import toast.event.process.ProcessCompleteEvent;
import toast.impl.ToastScheduler;
import toast.ui.model.ProcessTableModel;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

public class SimulationController extends PageController {

    @FXML
    private TableView<ProcessTableModel> table;
    @FXML
    private MFXButton startButton;

    @Override
    void init() {
        initTable();
        this.startButton.setOnAction(this::onStart);
        ToastEvent.registerListener(ProcessCompleteEvent.class, (e) -> Platform.runLater(this::updateTable));
    }

    @Override
    void exit() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @SuppressWarnings("unchecked")
    private void initTable() {
        var columnPID = new TableColumn<ProcessTableModel, Integer>("PID");
        var columnAT = new TableColumn<ProcessTableModel, Integer>("AT");
        var columnWL = new TableColumn<ProcessTableModel, Integer>("BT");
        var columnWT = new TableColumn<ProcessTableModel, Integer>("WT");
        var columnTT = new TableColumn<ProcessTableModel, Integer>("TT");
        var columnNTT = new TableColumn<ProcessTableModel, Double>("NTT");
        columnPID.setCellValueFactory(e -> e.getValue().getPid());
        columnAT.setCellValueFactory(e -> e.getValue().getArrivalTime());
        columnWL.setCellValueFactory(e -> e.getValue().getWorkload());
        columnWT.setCellValueFactory(e -> e.getValue().getWaitingTime());
        columnTT.setCellValueFactory(e -> e.getValue().getTurnaroundTime());
        columnNTT.setCellValueFactory(e -> e.getValue().getNormalizedTT());
        this.table.getColumns().addAll(columnPID, columnAT, columnWL, columnWT, columnTT, columnNTT);
        this.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void updateTable() {
        ObservableList<ProcessTableModel> items = this.table.getItems();
        Iterator<Process> iter = ToastScheduler.getInstance().getProcessList()
                .stream()
                .filter(Process::isComplete)
                .iterator();

        items.clear();

        while (iter.hasNext()) {
            items.add(new ProcessTableModel(iter.next()));
        }
    }

    private void onStart(ActionEvent event) {
        ToastScheduler scheduler = ToastScheduler.getInstance();

        if (!scheduler.isConfigured()) {
            String message = "Please go back to Settings page and fill out the forms.";
            Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
            alert.setHeaderText("Scheduler is not configured!");
            Platform.runLater(alert::showAndWait);
            return;
        }

        scheduler.start();
    }
}
