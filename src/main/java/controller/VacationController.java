package controller;

import dao.GenericDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.PublicServer;
import model.Vacation;
import util.AlertUtil;
import util.ValidationUtil;

import java.time.LocalDate;

public class VacationController {

    @FXML private ComboBox<PublicServer> cbServer;
    @FXML private TextField txtCoveredYear;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private TextField txtEnjoyedDays;
    @FXML private TextField txtPendingDays;

    @FXML private TableView<Vacation> table;
    @FXML private TableColumn<Vacation, Long> colId;
    @FXML private TableColumn<Vacation, String> colServer;
    @FXML private TableColumn<Vacation, Integer> colYear;
    @FXML private TableColumn<Vacation, String> colStartDate;
    @FXML private TableColumn<Vacation, Integer> colPendingDays;

    private final GenericDao<Vacation> dao = new GenericDao<>(Vacation.class);
    private final GenericDao<PublicServer> serverDao = new GenericDao<>(PublicServer.class);

    @FXML
    private void initialize() {
        cbServer.setItems(FXCollections.observableArrayList(serverDao.findAll()));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colServer.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getServer().toString()));
        colYear.setCellValueFactory(new PropertyValueFactory<>("coveredYear"));
        colStartDate.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getStartEnjoymentDate())));
        colPendingDays.setCellValueFactory(new PropertyValueFactory<>("pendingDays"));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        loadData();
    }

    @FXML
    private void save() {
        if (cbServer.getValue() == null || txtCoveredYear.getText().trim().isEmpty() || dpStartDate.getValue() == null
                || dpEndDate.getValue() == null || txtEnjoyedDays.getText().trim().isEmpty() || txtPendingDays.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Todos los campos de vacaciones son obligatorios.");
            return;
        }

        Integer year = ValidationUtil.parsePositiveInt(txtCoveredYear.getText().trim());
        Integer enjoyedDays = ValidationUtil.parseNonNegativeInt(txtEnjoyedDays.getText().trim());
        Integer pendingDays = ValidationUtil.parseNonNegativeInt(txtPendingDays.getText().trim());
        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();

        if (year == null || year < 1990 || year > LocalDate.now().getYear() + 1) {
            showAlert(Alert.AlertType.WARNING, "Dato inválido", "El período (año) debe estar entre 1990 y el próximo año.");
            return;
        }

        if (!ValidationUtil.isDateRangeValid(startDate, endDate)) {
            showAlert(Alert.AlertType.WARNING, "Fechas inválidas", "La fecha fin de disfrute no puede ser anterior al inicio.");
            return;
        }

        if (enjoyedDays == null || pendingDays == null) {
            showAlert(Alert.AlertType.WARNING, "Dato inválido", "Días disfrutados y saldo pendiente deben ser números válidos (0 o más).");
            return;
        }

        Vacation vacation = new Vacation();
        vacation.setServer(cbServer.getValue());
        vacation.setStartEnjoymentDate(startDate);
        vacation.setEndEnjoymentDate(endDate);
        vacation.setCoveredYear(year);
        vacation.setEnjoyedDays(enjoyedDays);
        vacation.setPendingDays(pendingDays);

        if (dao.save(vacation)) {
            loadData();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Vacaciones guardadas correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No fue posible guardar las vacaciones.");
        }
    }

    @FXML
    private void delete() {
        Vacation selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Selecciona un registro para eliminar.");
            return;
        }

        if (dao.delete(selected.getId())) {
            loadData();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Registro eliminado correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No fue posible eliminar el registro.");
        }
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(dao.findAll()));
    }

    private void clearFields() {
        cbServer.setValue(null);
        txtCoveredYear.clear();
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
        txtEnjoyedDays.clear();
        txtPendingDays.clear();
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) table.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        AlertUtil.show(type, title, message);
    }
}